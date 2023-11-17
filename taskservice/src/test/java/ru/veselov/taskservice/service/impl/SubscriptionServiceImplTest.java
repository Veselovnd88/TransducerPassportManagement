package ru.veselov.taskservice.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;
import ru.veselov.taskservice.model.Task;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    SubscriptionsStorage subscriptionsStorage;

    @InjectMocks
    SubscriptionServiceImpl subscriptionService;

    @Captor
    ArgumentCaptor<ServerSentEvent<StatusStreamMessage>> sseCaptor;

    @Test
    void shouldSaveSubscription() {
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        subscriptionService.saveSubscription(subscriptionData);

        Mockito.verify(subscriptionsStorage).saveSubscription(subscriptionData);
    }

    @Test
    void shouldRemoveSubscription() {
        subscriptionService.removeSubscription(TestUtils.SUB_ID);

        Mockito.verify(subscriptionsStorage).removeSubscription(TestUtils.SUB_ID);
    }

    @Test
    void shouldCompleteSubscriptionsByTask() {
        Task task = TestUtils.getTask();
        task.setStatus(TaskStatus.FAILED);
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(List.of(subscriptionData));

        subscriptionService.completeSubscriptionsByTask(task);

        Mockito.verify(subscriptionData.getFluxSink()).complete();
    }

    @Test
    void shouldNotCompleteSubscriptionsIfEmptyList() {
        Task task = TestUtils.getTask();
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(Collections.emptyList());

        subscriptionService.completeSubscriptionsByTask(task);

        Mockito.verifyNoInteractions(subscriptionData.getFluxSink());
    }

    @Test
    void shouldSendMessageToTaskSubscriptions() {
        Task task = TestUtils.getTask();
        task.setStatus(TaskStatus.FAILED);
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(List.of(subscriptionData));
        StatusStreamMessage statusStreamMessage = TestUtils.getStatusStreamMessage();

        subscriptionService.sendMessageToSubscriptionsByTask(statusStreamMessage, EventType.UPDATED);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionData.getFluxSink()).next(sseCaptor.capture()),
                () -> {
                    ServerSentEvent<StatusStreamMessage> captured = sseCaptor.getValue();
                    Assertions.assertThat(captured.event()).isEqualTo(EventType.UPDATED.toString());
                    Assertions.assertThat(captured.data()).isEqualTo(statusStreamMessage);
                }
        );
    }

    @Test
    void shouldNotSendMessageIfListOfSubscriptionsIsEmpty() {
        Task task = TestUtils.getTask();
        task.setStatus(TaskStatus.FAILED);
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(Collections.emptyList());
        StatusStreamMessage statusStreamMessage = TestUtils.getStatusStreamMessage();

        subscriptionService.sendMessageToSubscriptionsByTask(statusStreamMessage, EventType.UPDATED);

        Mockito.verifyNoInteractions(subscriptionData.getFluxSink());
    }

    @Test
    void shouldSendErrorMessageToSubscriptions() {
        Task task = TestUtils.getTask();
        task.setStatus(TaskStatus.FAILED);
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(List.of(subscriptionData));
        StatusStreamMessage statusStreamMessage = TestUtils.getStatusStreamMessage();

        subscriptionService.sendErrorMessageToSubscriptionsByTask(statusStreamMessage);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionData.getFluxSink()).next(sseCaptor.capture()),
                () -> {
                    ServerSentEvent<StatusStreamMessage> captured = sseCaptor.getValue();
                    Assertions.assertThat(captured.event()).isEqualTo(EventType.ERROR.toString());
                    Assertions.assertThat(captured.data()).isEqualTo(statusStreamMessage);
                },
                () -> Mockito.verify(subscriptionData.getFluxSink()).complete()
        );
    }

    @Test
    void shouldNotSendErrorIfListIsEmpty() {
        Task task = TestUtils.getTask();
        task.setStatus(TaskStatus.FAILED);
        SubscriptionData subscriptionData = TestUtils.getSubscriptionData();
        Mockito.when(subscriptionsStorage.findSubscriptionsByTask(TestUtils.TASK_ID_STR))
                .thenReturn(Collections.emptyList());
        StatusStreamMessage statusStreamMessage = TestUtils.getStatusStreamMessage();

        subscriptionService.sendErrorMessageToSubscriptionsByTask(statusStreamMessage);

        Mockito.verifyNoInteractions(subscriptionData.getFluxSink());
    }

}
