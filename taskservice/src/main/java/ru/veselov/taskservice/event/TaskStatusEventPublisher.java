package ru.veselov.taskservice.event;

public interface TaskStatusEventPublisher {

    void publishTaskStatus(String taskId, EventType eventType);

}
