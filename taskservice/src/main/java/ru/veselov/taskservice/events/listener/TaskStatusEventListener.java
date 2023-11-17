package ru.veselov.taskservice.events.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.TaskStatusEvent;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskService;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventListener {

    private final TaskService taskService;

    @EventListener(TaskStatusEvent.class)
    void handleTaskStatusEvent(TaskStatusEvent taskStatusEvent) {
        String taskId = taskStatusEvent.getTaskId();
        Task task = taskService.getTask(taskId);
        if (task.getStatus().equals(TaskStatus.PERFORMED)) {
            ServerSentEvent<Task> performedTaskEvent = ServerSentEvent
                    .builder(task)
                    .event(task.getStatus().toString()).build();
        }
    }
}
