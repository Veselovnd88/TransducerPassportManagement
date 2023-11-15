package ru.veselov.taskservice.events;

import java.util.Optional;
import java.util.UUID;

public interface SubscriptionsStorage {

    void saveSubscription(SubscriptionData subscription);

    void removeSubscription(UUID subId);

    Optional<SubscriptionData> findSubscription(UUID subId);


}
