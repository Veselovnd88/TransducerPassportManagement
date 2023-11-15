package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;
import ru.veselov.taskservice.service.SubscriptionService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionsStorage subscriptionsStorage;

    @Override
    public void saveSubscription(SubscriptionData subscription) {
        subscriptionsStorage.saveSubscription(subscription);
        log.info("Subscription for [task: {}] status saved in storage", subscription.getTaskId());
    }

    @Override
    public void removeSubscription(UUID subId) {
        subscriptionsStorage.removeSubscription(subId);
    }

}
