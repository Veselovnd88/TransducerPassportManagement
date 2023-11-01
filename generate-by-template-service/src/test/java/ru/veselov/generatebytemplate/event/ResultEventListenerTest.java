package ru.veselov.generatebytemplate.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.service.KafkaBrokerSender;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultEventListenerTest {

    @Mock
    KafkaBrokerSender kafkaBrokerSender;

    @InjectMocks
    ResultEventListener resultEventListener;

    @Test
    void shouldHandleSuccessEvent() {
        SuccessResultEvent successResultEvent = new SuccessResultEvent(
                UUID.fromString(TestUtils.TASK_ID),
                TestUtils.FILE_ID,
                "message");

        resultEventListener.handleSuccessResultEvent(successResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID, "message", null, EventType.READY);
        Mockito.verify(kafkaBrokerSender, Mockito.times(1)).sendResultMessage(TestUtils.TASK_ID, taskResultDto);
    }

    @Test
    void shouldHandleErrorEvent() {
        ErrorResultEvent errorResultEvent = new ErrorResultEvent(
                UUID.fromString(TestUtils.TASK_ID), "errorMessage", "message");

        resultEventListener.handleErrorResultEvent(errorResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(null, "message", "errorMessage", EventType.ERROR);
        Mockito.verify(kafkaBrokerSender, Mockito.times(1)).sendResultMessage(TestUtils.TASK_ID, taskResultDto);
    }

}