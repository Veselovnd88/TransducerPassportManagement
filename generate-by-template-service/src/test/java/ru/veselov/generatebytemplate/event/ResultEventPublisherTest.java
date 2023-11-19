package ru.veselov.generatebytemplate.event;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.model.ResultFile;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultEventPublisherTest {

    private static final String MESSAGE = "message";

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    ResultEventPublisher resultEventPublisher;

    @Captor
    ArgumentCaptor<SuccessResultEvent> successArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorResultEvent> eventArgumentCaptor;

    @Test
    void publishSuccessResultEvent() {
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();

        resultEventPublisher.publishSuccessResultEvent(resultFile);

        Mockito.verify(applicationEventPublisher).publishEvent(successArgumentCaptor.capture());
        SuccessResultEvent captured = successArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.getResultFileId()).isEqualTo(resultFile.getId().toString()),
                () -> Assertions.assertThat(captured.getTaskId().toString()).hasToString(resultFile.getTaskId()),
                () -> Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.READY),
                () -> Assertions.assertThat(captured.getMessage()).isNotBlank()
        );

    }

    @Test
    void publishErrorResultEvent() {
        resultEventPublisher.publishErrorResultEvent(TestUtils.TASK_ID_STR, new Exception(MESSAGE));

        Mockito.verify(applicationEventPublisher).publishEvent(eventArgumentCaptor.capture());
        ErrorResultEvent captured = eventArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.getTaskId()).isEqualTo(UUID.fromString(TestUtils.TASK_ID_STR)),
                () -> Assertions.assertThat(captured.getErrorMessage()).isEqualTo(MESSAGE),
                () -> Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.ERROR),
                () -> Assertions.assertThat(captured.getMessage()).isNotBlank()
        );

    }
}