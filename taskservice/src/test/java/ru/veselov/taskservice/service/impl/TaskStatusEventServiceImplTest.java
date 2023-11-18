package ru.veselov.taskservice.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.veselov.taskservice.utils.TestUtils;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.service.SubscriptionService;

@ExtendWith(MockitoExtension.class)
class TaskStatusEventServiceImplTest {

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    TaskStatusEventServiceImpl taskStatusEventService;

    @Captor
    ArgumentCaptor<SubscriptionData> subscriptionDataCaptor;

    @Test
    void shouldReturnAdnSaveFluxSubscription() {
        Flux<ServerSentEvent<StatusStreamMessage>> eventStream = taskStatusEventService
                .createSubscription(TestUtils.TASK_ID_STR);
        StepVerifier.create(eventStream.take(1)).expectNextMatches(event -> {
                    assert event.event() != null;
                    return event.event().equals(EventType.CONNECTED.toString());
                })
                .verifyComplete();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(subscriptionService).saveSubscription(subscriptionDataCaptor.capture()),
                () -> {
                    SubscriptionData captured = subscriptionDataCaptor.getValue();
                    Assertions.assertThat(captured.getTaskId()).isEqualTo(TestUtils.TASK_ID_STR);
                }
        );
    }

    @Test
    void shouldRemoveSubscriptionOnDispose() {
        Flux<ServerSentEvent<StatusStreamMessage>> eventStream = taskStatusEventService
                .createSubscription(TestUtils.TASK_ID_STR);

        eventStream.subscribe().dispose();

        Mockito.verify(subscriptionService).removeSubscription(Mockito.any());
    }

}
