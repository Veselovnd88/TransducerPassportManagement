package ru.veselov.taskservice.events.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SubscriptionsStorageImpl implements SubscriptionsStorage {

    private final Map<UUID, SubscriptionData> subscriptionMap = new ConcurrentHashMap<>();

    @Override
    public void saveSubscription(SubscriptionData subscription) {
        subscriptionMap.put(subscription.getSubId(), subscription);
    }

    @Override
    public void removeSubscription(UUID subId) {
        subscriptionMap.remove(subId);
    }

    @Override
    public List<SubscriptionData> findSubscriptionsByTask(String taskId) {
        return subscriptionMap.values().stream().filter(sub -> sub.getTaskId().equals(taskId)).toList();
    }

    @Override
    public Optional<SubscriptionData> findSubscription(UUID subId) {
        return Optional.of(subscriptionMap.get(subId));
    }

}
