package ru.veselov.taskservice.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.event.StatusStreamMessage;
import ru.veselov.taskservice.event.TaskStatusEvent;
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.SubscriptionService;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class TaskStatusEventListener {

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final SubscriptionService subscriptionService;

    @EventListener
    void handleTaskStatusEvent(TaskStatusEvent taskStatusEvent) {
        log.info("Handled [event: {}]", taskStatusEvent);
        String taskId = taskStatusEvent.getTaskId();
        Optional<TaskEntity> taskOptional = taskRepository.findById(UUID.fromString(taskId));
        if (taskOptional.isPresent()) {
            TaskEntity taskEntity = taskOptional.get();
            Task task = taskMapper.toModel(taskEntity);
            StatusStreamMessage statusStreamMessage = getStreamMessage(taskId, task);
            subscriptionService.sendMessageToSubscriptionsByTask(statusStreamMessage, taskStatusEvent.getEventType());
            TaskStatus taskStatus = task.getStatus();
            if (taskStatus.equals(TaskStatus.PERFORMED) || taskStatus.equals(TaskStatus.FAILED)) {
                subscriptionService.completeSubscriptionsByTask(task);
            }
        } else {
            StatusStreamMessage statusStreamMessage = getErrorStreamMessage(taskId);
            log.error("[Task {}] not found, closing event stream", taskId);
            subscriptionService.sendErrorMessageToSubscriptionsByTask(statusStreamMessage);
        }
    }

    private StatusStreamMessage getErrorStreamMessage(String taskId) {
        StatusStreamMessage statusStreamMessage = new StatusStreamMessage();
        statusStreamMessage.setTaskId(taskId);
        statusStreamMessage.setMessage("Task %s not found, connection will be closed".formatted(taskId));
        return statusStreamMessage;
    }

    private StatusStreamMessage getStreamMessage(String taskId, Task task) {
        StatusStreamMessage statusStreamMessage = new StatusStreamMessage();
        statusStreamMessage.setMessage("Task %s status: %s".formatted(taskId, task.getStatus()));
        statusStreamMessage.setTask(task);
        statusStreamMessage.setTaskId(taskId);
        return statusStreamMessage;
    }

}
