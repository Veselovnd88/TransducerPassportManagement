package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.events.TaskStatusEventPublisher;
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

    private final TaskStatusEventPublisher taskStatusEventPublisher;

    @Override
    public Flux<ServerSentEvent<Task>> createSubscription(String taskId) {
        return Flux.create(fluxsink -> {
            log.info("Create status event stream for task: {}", taskId);
            UUID subId = UUID.randomUUID();
            fluxsink.onDispose(removeSubscription(subId));
            SubscriptionData subscriptionData = new SubscriptionData(subId, taskId, fluxsink);
            subscriptionService.saveSubscription(subscriptionData);
            ServerSentEvent<Task> initEvent = createInitSSE(taskId);
            fluxsink.next(initEvent);
            log.info("Connected event sent to new subscription of task: {}", taskId);
            taskStatusEventPublisher.publishTaskStatus(taskId, EventType.CONNECTED);
        });
    }

    private Disposable removeSubscription(UUID subId) {
        return () -> subscriptionService.removeSubscription(subId);
    }

    private ServerSentEvent<Task> createInitSSE(String taskId) {
        Task initTaskInfo = new Task();
        initTaskInfo.setTaskId(UUID.fromString(taskId));
        initTaskInfo.setStatus(TaskStatus.STARTED);
        return ServerSentEvent
                .builder(initTaskInfo).event(EventType.CONNECTED.toString())
                .comment("Task %s status event stream".formatted(taskId))
                .build();

    }

}
