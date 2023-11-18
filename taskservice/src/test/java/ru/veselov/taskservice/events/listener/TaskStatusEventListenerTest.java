package ru.veselov.taskservice.events.listener;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.TaskStatusEvent;
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.mapper.TaskMapperImpl;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.SubscriptionService;
import ru.veselov.taskservice.utils.TestUtils;
import ru.veselov.taskservice.utils.argumentproviders.TaskStatusArgumentProvider;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TaskStatusEventListenerTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    TaskStatusEventListener taskStatusEventListener;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(taskStatusEventListener, "taskMapper", new TaskMapperImpl(), TaskMapper.class);
    }

    @Captor
    ArgumentCaptor<StatusStreamMessage> messageArgumentCaptor;

    @Test
    void shouldHandlerEventAndSendMessage() {
        TaskStatusEvent taskStatusEvent = new TaskStatusEvent(TestUtils.TASK_ID_STR, EventType.UPDATED);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(TestUtils.TASK_ID);
        taskEntity.setStatus(TaskStatus.CREATED);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(taskEntity));

        taskStatusEventListener.handleTaskStatusEvent(taskStatusEvent);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionService)
                        .sendMessageToSubscriptionsByTask(messageArgumentCaptor.capture(), Mockito.any()),
                () -> {
                    StatusStreamMessage captured = messageArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getTask().getTaskId()).isEqualTo(taskEntity.getTaskId());
                    Assertions.assertThat(captured.getTaskId()).isEqualTo(taskEntity.getTaskId().toString());
                }
        );
    }

    @ParameterizedTest
    @ArgumentsSource(TaskStatusArgumentProvider.class)
    void shouldHandlerEventAndSendMessageAndCompleteSubs(TaskStatus taskStatus) {
        TaskStatusEvent taskStatusEvent = new TaskStatusEvent(TestUtils.TASK_ID_STR, EventType.UPDATED);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(TestUtils.TASK_ID);
        taskEntity.setStatus(taskStatus);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(taskEntity));
        Task task = new Task();
        task.setTaskId(TestUtils.TASK_ID);
        task.setStatus(taskStatus);
        taskStatusEventListener.handleTaskStatusEvent(taskStatusEvent);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionService)
                        .sendMessageToSubscriptionsByTask(messageArgumentCaptor.capture(), Mockito.any()),
                () -> {
                    StatusStreamMessage captured = messageArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getTask().getTaskId()).isEqualTo(taskEntity.getTaskId());
                    Assertions.assertThat(captured.getTaskId()).isEqualTo(taskEntity.getTaskId().toString());
                },
                () -> Mockito.verify(subscriptionService).completeSubscriptionsByTask(task)
        );
    }

    @Test
    void shouldHandleEventAndSendErrorMessageToSubs() {
        TaskStatusEvent taskStatusEvent = new TaskStatusEvent(TestUtils.TASK_ID_STR, EventType.UPDATED);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.empty());

        taskStatusEventListener.handleTaskStatusEvent(taskStatusEvent);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionService)
                        .sendErrorMessageToSubscriptionsByTask(messageArgumentCaptor.capture()),
                () -> {
                    StatusStreamMessage captured = messageArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getTaskId()).isEqualTo(TestUtils.TASK_ID_STR);
                    Assertions.assertThat(captured.getTask()).isNull();
                }
        );

    }


}