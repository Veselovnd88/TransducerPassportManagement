package ru.veselov.taskservice.events;

import ru.veselov.taskservice.model.Task;

public interface TaskStatusEventPublisher {

    void publishTaskStatus(Task task, EventType eventType);

}
