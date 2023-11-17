package ru.veselov.taskservice.service;

import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;

import java.util.UUID;

public interface SubscriptionService {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(UUID subId);

    void completeSubscriptionsByTask(String taskId);

    void sendMessageToSubscriptionsByTask(StatusStreamMessage message, EventType eventType);

    void sendErrorMessageToSubscriptionsByTask(StatusStreamMessage message, EventType eventType);

}
