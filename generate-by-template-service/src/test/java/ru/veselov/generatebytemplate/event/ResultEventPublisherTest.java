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
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.model.ResultFile;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultEventPublisherTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    ResultEventPublisher resultEventPublisher;

    @Captor
    ArgumentCaptor<SuccessResultEvent> successArgumentCaptor;

    @Captor
    ArgumentCaptor<ErrorResultEvent> errorResultEventArgumentCaptor;

    @Test
    void publishSuccessResultEvent() {
        ResultFile resultFile = TestUtils.getBasicGeneratedResultFile();

        resultEventPublisher.publishSuccessResultEvent(resultFile);

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(successArgumentCaptor.capture());
        SuccessResultEvent captured = successArgumentCaptor.getValue();
        Assertions.assertThat(captured.getResultFileId()).isEqualTo(resultFile.getId().toString());
        Assertions.assertThat(captured.getTaskId().toString()).hasToString(resultFile.getTaskId());
        Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.READY);
        Assertions.assertThat(captured.getMessage()).isNotBlank();
    }

    @Test
    void publishErrorResultEvent() {
        resultEventPublisher.publishErrorResultEvent(TestUtils.TASK_ID, new Exception("message"));

        Mockito.verify(applicationEventPublisher, Mockito.times(1)).publishEvent(errorResultEventArgumentCaptor.capture());
        ErrorResultEvent captured = errorResultEventArgumentCaptor.getValue();
        Assertions.assertThat(captured.getTaskId()).isEqualTo(UUID.fromString(TestUtils.TASK_ID));
        Assertions.assertThat(captured.getErrorMessage()).isEqualTo("message");
        Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.ERROR);
        Assertions.assertThat(captured.getMessage()).isNotBlank();
    }
}