package ru.veselov.taskservice.events.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.TaskStatusEvent;
import ru.veselov.taskservice.events.TaskStatusEventPublisher;
import ru.veselov.taskservice.model.Task;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventPublisherImpl implements TaskStatusEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishTaskStatus(Task task, EventType eventType) {
        TaskStatusEvent event = new TaskStatusEvent(task, eventType);
        publisher.publishEvent(event);
        log.info("Event: {} for task: {} published", task.getTaskId(), eventType);
    }

}
