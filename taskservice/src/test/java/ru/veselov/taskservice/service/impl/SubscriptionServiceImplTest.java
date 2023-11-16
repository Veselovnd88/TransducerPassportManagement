package ru.veselov.taskservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceImplTest {

    @Mock
    SubscriptionsStorage subscriptionsStorage;

    @InjectMocks
    SubscriptionServiceImpl subscriptionService;

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

}
