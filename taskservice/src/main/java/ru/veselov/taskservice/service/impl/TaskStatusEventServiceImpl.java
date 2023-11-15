package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.events.SubscriptionData;
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
            UUID subId = UUID.randomUUID();
            SubscriptionData subscriptionData = new SubscriptionData(subId, taskId, fluxsink);
            log.info("Create status stream for task: {}", taskId);
            fluxsink.onCancel(removeSubscription(subId));
            fluxsink.onDispose(removeSubscription(subId));
            Task task = taskService.getTask(taskId);
            ServerSentEvent<String> connectEvent = ServerSentEvent
                    .builder("Task: %s status is: %s".formatted(task.getTaskId(), task.getStatus()))
                    .event(task.getStatus().toString())
                    .build();
            fluxsink.next(connectEvent);
            subscriptionService.saveSubscription(subscriptionData);
        });
    }

    private Disposable removeSubscription(UUID subId) {
        return () -> subscriptionService.removeSubscription(subId);
    }
}
