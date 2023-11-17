package ru.veselov.taskservice.events;

public interface TaskStatusEventPublisher {

    void publishTaskStatus(String taskId, EventType eventType);

}
