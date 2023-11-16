package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;
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
    public void completeSubscriptionsByTask(String taskId) {
        List<SubscriptionData> subscriptionsByTask = subscriptionsStorage.findSubscriptionsByTask(taskId);
        if (!subscriptionsByTask.isEmpty()) {
            subscriptionsByTask.forEach(sub -> sub.getFluxSink().complete());
            log.info("Subscriptions for status [task: {}] completed, task already performed", taskId);
        }

    }

    @Override
    public void doNextSubscriptionsByTask(String taskId, TaskStatus status) {
        List<SubscriptionData> subscriptionsByTask = subscriptionsStorage.findSubscriptionsByTask(taskId);
        if (!subscriptionsByTask.isEmpty()) {
            ServerSentEvent<String> serverSentEvent = ServerSentEvent.builder("Task change status for task: %s, status is %s"
                    .formatted(taskId, status)).event(status.toString()).build();
            subscriptionsByTask.forEach(sub -> sub.getFluxSink().next(serverSentEvent));
        }
    }
}
