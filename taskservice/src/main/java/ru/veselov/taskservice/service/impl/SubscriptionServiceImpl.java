package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.SubscriptionService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionsStorage subscriptionsStorage;

    @Override
    public void saveSubscription(SubscriptionData subscription) {
        subscriptionsStorage.saveSubscription(subscription);
        log.info("[Subscription for task: {}] status saved in storage", subscription.getTaskId());
    }

    @Override
    public void removeSubscription(UUID subId) {
        subscriptionsStorage.removeSubscription(subId);
        log.info("[Subscription: {}] removed from storage", subId);
    }

    @Override
    public void completeSubscriptionsByTask(Task task) {
        String taskId = task.getTaskId().toString();
        List<SubscriptionData> subscriptionsByTask = subscriptionsStorage.findSubscriptionsByTask(taskId);
        if (!subscriptionsByTask.isEmpty()) {
            subscriptionsByTask.forEach(sub -> sub.getFluxSink().complete());
            log.info("Subscriptions for [task: {}] completed, task has [status: {}]", taskId, task.getStatus());
        }
    }

    @Override
    public void sendMessageToSubscriptionsByTask(StatusStreamMessage streamMessage, EventType eventType) {
        String taskId = streamMessage.getTaskId();
        List<SubscriptionData> subscriptionsByTask = subscriptionsStorage
                .findSubscriptionsByTask(taskId);
        if (!subscriptionsByTask.isEmpty()) {
            ServerSentEvent<StatusStreamMessage> serverSentEvent = ServerSentEvent.builder(streamMessage)
                    .event(eventType.toString()).build();
            subscriptionsByTask.forEach(sub -> sub.getFluxSink().next(serverSentEvent));
            log.info("Event sent for [{} subscriptions] of [task :{}], [status: {}]",
                    subscriptionsByTask.size(), taskId, streamMessage.getTask().getStatus());
        }
    }

    @Override
    public void sendErrorMessageToSubscriptionsByTask(StatusStreamMessage streamMessage) {
        String taskId = streamMessage.getTaskId();
        List<SubscriptionData> subscriptionsByTask = subscriptionsStorage
                .findSubscriptionsByTask(taskId);
        if (!subscriptionsByTask.isEmpty()) {
            ServerSentEvent<StatusStreamMessage> serverSentEvent = ServerSentEvent.builder(streamMessage)
                    .event(EventType.ERROR.toString()).build();
            subscriptionsByTask.forEach(sub -> {
                sub.getFluxSink().next(serverSentEvent);
                sub.getFluxSink().complete();
            });
            log.info("Sent error message, connection closed for [{} subscriptions] of [task :{}], [message: {}]",
                    subscriptionsByTask.size(), taskId, streamMessage.getMessage());
        }
    }

}

