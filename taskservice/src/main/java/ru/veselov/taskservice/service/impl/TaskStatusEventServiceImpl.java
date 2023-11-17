package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.TaskStatusEventPublisher;
import ru.veselov.taskservice.service.SubscriptionService;
import ru.veselov.taskservice.service.TaskStatusEventService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventServiceImpl implements TaskStatusEventService {

    private final SubscriptionService subscriptionService;

    private final TaskStatusEventPublisher taskStatusEventPublisher;

    @Override
    public Flux<ServerSentEvent<StatusStreamMessage>> createSubscription(String taskId) {
        return Flux.create(fluxsink -> {
            log.info("Create status event stream for [task: {}]", taskId);
            UUID subId = UUID.randomUUID();
            fluxsink.onDispose(removeSubscription(subId));
            SubscriptionData subscriptionData = new SubscriptionData(subId, taskId, fluxsink);
            subscriptionService.saveSubscription(subscriptionData);
            ServerSentEvent<StatusStreamMessage> initEvent = createInitSSE(taskId);
            fluxsink.next(initEvent);
            log.info("Connected event sent to new subscription of task: {}", taskId);
            taskStatusEventPublisher.publishTaskStatus(taskId, EventType.CONNECTED);
        });
    }

    private Disposable removeSubscription(UUID subId) {
        return () -> subscriptionService.removeSubscription(subId);
    }

    private ServerSentEvent<StatusStreamMessage> createInitSSE(String taskId) {
        StatusStreamMessage statusStreamMessage = new StatusStreamMessage();
        statusStreamMessage.setTaskId(taskId);
        statusStreamMessage.setMessage("Task %s status event stream".formatted(taskId));
        return ServerSentEvent
                .builder(statusStreamMessage).event(EventType.CONNECTED.toString())
                .build();
    }

}
