package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.exception.TaskNotStartedException;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;
import ru.veselov.taskservice.service.TaskLaunchService;
import ru.veselov.taskservice.service.TaskService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskLaunchServiceImpl implements TaskLaunchService {

    public static final String TASK_NOT_STARTED_LOG_MSG =
            "Task [id: {}] for [{}] was not started due to error with generate service";

    public static final String TASK_NOT_STARTED_EXCEPTION_MSG =
            "Task [id: %s] for [%s] was not started due to error with generate service";

    private final TaskService taskService;

    private final GenerateServiceHttpClient generateServiceHttpClient;

    @Override
    public Task startTask(GeneratePassportsDto generatePassportsDto, String username) {
        Task createdTask = taskService.createTask(generatePassportsDto, username);
        boolean accepted = generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, createdTask);
        if (accepted) {
            Task startedTask = taskService.updateStatusToStart(createdTask.getTaskId());
            log.info("Task [id: {}] was successfully started", createdTask.getTaskId());
            return startedTask;
        } else {
            //TODO implement scheduled service for deleting not started tasks
            log.error(TASK_NOT_STARTED_LOG_MSG, createdTask.getTaskId(), username);
            throw new TaskNotStartedException(TASK_NOT_STARTED_EXCEPTION_MSG.formatted(createdTask.getTaskId(), username));
        }
    }

}
