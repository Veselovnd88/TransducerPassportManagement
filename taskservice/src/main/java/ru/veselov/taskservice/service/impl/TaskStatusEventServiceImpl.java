package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.SubscriptionsStorage;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.SubscriptionService;
import ru.veselov.taskservice.service.TaskService;
import ru.veselov.taskservice.service.TaskStatusEventService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventServiceImpl implements TaskStatusEventService {

    private final SubscriptionService subscriptionService;

    private final TaskService taskService;

    @Override
    public Flux<ServerSentEvent<String>> createSubscription(String taskId) {
        return Flux.create(fluxsink -> {
            log.info("Create status stream for task: {}", taskId);
            UUID subId = UUID.randomUUID();
            fluxsink.onDispose(removeSubscription(subId));
            SubscriptionData subscriptionData = new SubscriptionData(subId, taskId, fluxsink);
            subscriptionService.saveSubscription(subscriptionData);
            ServerSentEvent<String> initEvent = ServerSentEvent
                    .builder("Connected to stream of status events for task : %s".formatted(taskId))
                    .build();
            fluxsink.next(initEvent);
        });
    }

    private Disposable removeSubscription(UUID subId) {
        return () -> subscriptionService.removeSubscription(subId);
    }

}
