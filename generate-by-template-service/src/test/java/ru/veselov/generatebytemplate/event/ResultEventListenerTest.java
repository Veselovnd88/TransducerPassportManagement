package ru.veselov.generatebytemplate.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.service.KafkaBrokerSender;
import ru.veselov.generatebytemplate.utils.TestUtils;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultEventListenerTest {

    private static final String ERROR_MESSAGE = "errorMessage";

    @Mock
    KafkaBrokerSender kafkaBrokerSender;

    @InjectMocks
    ResultEventListener resultEventListener;

    @Test
    void shouldHandleSuccessEvent() {
        SuccessResultEvent successResultEvent = new SuccessResultEvent(
                UUID.fromString(TestUtils.TASK_ID_STR),
                TestUtils.FILE_ID
        );

        resultEventListener.handleSuccessResultEvent(successResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID, null, TaskStatus.PERFORMED);
        Mockito.verify(kafkaBrokerSender, Mockito.times(1)).sendResultMessage(TestUtils.TASK_ID_STR, taskResultDto);
    }

    @Test
    void shouldHandleErrorEvent() {
        ErrorResultEvent errorResultEvent = new ErrorResultEvent(
                UUID.fromString(TestUtils.TASK_ID_STR), ERROR_MESSAGE);

        resultEventListener.handleErrorResultEvent(errorResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(null, ERROR_MESSAGE, TaskStatus.FAILED);
        Mockito.verify(kafkaBrokerSender).sendResultMessage(TestUtils.TASK_ID_STR, taskResultDto);
    }

}