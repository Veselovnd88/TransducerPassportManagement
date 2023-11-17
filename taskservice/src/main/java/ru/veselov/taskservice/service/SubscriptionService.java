package ru.veselov.taskservice.service;

import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.model.Task;

import java.util.UUID;

public interface SubscriptionService {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(UUID subId);

    void completeSubscriptionsByTask(Task task);

    void sendMessageToSubscriptionsByTask(StatusStreamMessage message, EventType eventType);

    void sendErrorMessageToSubscriptionsByTask(StatusStreamMessage message);

}
