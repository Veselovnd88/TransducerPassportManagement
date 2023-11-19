package ru.veselov.taskservice.event;

import java.util.List;
import java.util.UUID;

public interface SubscriptionsStorage {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(UUID subId);

    List<SubscriptionData> findSubscriptionsByTask(String taskId);

}