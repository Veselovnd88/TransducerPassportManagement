package ru.veselov.taskservice.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.event.EventType;
import ru.veselov.taskservice.event.TaskStatusEvent;
import ru.veselov.taskservice.event.TaskStatusEventPublisher;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventPublisherImpl implements TaskStatusEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishTaskStatus(String taskId, EventType eventType) {
        log.info("[Publishing event: {}] for [task: {}]", eventType, taskId);
        TaskStatusEvent event = new TaskStatusEvent(taskId, eventType);
        publisher.publishEvent(event);
    }

}
