package ru.veselov.generatebytemplate.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.TaskResultDto;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class ResultEventListenerTest {

    @Mock
    KafkaTemplate<String, TaskResultDto> kafkaTemplate;

    @InjectMocks
    ResultEventListener resultEventListener;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(resultEventListener, "taskTopic", "task", String.class);
    }

    @Test
    void shouldHandleSuccessEvent() {
        SuccessResultEvent successResultEvent = new SuccessResultEvent(
                UUID.fromString(TestUtils.TASK_ID),
                TestUtils.FILE_ID,
                "message");

        resultEventListener.handleSuccessResultEvent(successResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID, "message", null, EventType.READY);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send("task", TestUtils.TASK_ID, taskResultDto);
    }

    @Test
    void shouldHandleErrorEvent() {
        ErrorResultEvent errorResultEvent = new ErrorResultEvent(
                UUID.fromString(TestUtils.TASK_ID), "errorMessage", "message");

        resultEventListener.handleErrorResultEvent(errorResultEvent);

        TaskResultDto taskResultDto = new TaskResultDto(null, "message", "errorMessage", EventType.ERROR);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send("task", TestUtils.TASK_ID, taskResultDto);
    }

}