package ru.veselov.taskservice.event.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ru.veselov.taskservice.util.TestUtils;
import ru.veselov.taskservice.event.EventType;
import ru.veselov.taskservice.event.TaskStatusEvent;

@ExtendWith(MockitoExtension.class)
class TaskStatusEventPublisherImplTest {

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    TaskStatusEventPublisherImpl taskStatusEventPublisher;

    @Captor
    ArgumentCaptor<TaskStatusEvent> eventArgumentCaptor;

    @Test
    void shouldCreateAndPublishEvent() {
        taskStatusEventPublisher.publishTaskStatus(TestUtils.TASK_ID_STR, EventType.UPDATED);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(applicationEventPublisher).publishEvent(eventArgumentCaptor.capture()),
                () -> {
                    TaskStatusEvent captured = eventArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getTaskId()).isEqualTo(TestUtils.TASK_ID_STR);
                    Assertions.assertThat(captured.getEventType()).isEqualTo(EventType.UPDATED);
                }
        );
    }

}