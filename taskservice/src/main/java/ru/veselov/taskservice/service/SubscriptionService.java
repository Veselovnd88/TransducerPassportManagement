package ru.veselov.taskservice.service;

import ru.veselov.taskservice.events.SubscriptionData;

import java.util.UUID;

public interface SubscriptionService {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(UUID subId);

}
