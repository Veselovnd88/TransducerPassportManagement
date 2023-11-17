package ru.veselov.taskservice.events;

import ru.veselov.taskservice.model.Task;

public interface TaskStatusEventPublisher {

    void publishTaskStatus(String taskId, EventType eventType);

}
