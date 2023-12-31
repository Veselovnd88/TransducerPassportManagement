package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;
import ru.veselov.taskservice.service.TaskLaunchService;
import ru.veselov.taskservice.service.TaskService;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskLaunchServiceImpl implements TaskLaunchService {

    private final TaskService taskService;

    private final GenerateServiceHttpClient generateServiceHttpClient;

    @Override
    public Task launchTask(GeneratePassportsDto generatePassportsDto, String username) {
        Task createdTask = taskService.createTask(generatePassportsDto, username);
        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, createdTask, username);
        Task launchedTask = taskService.updateStatus(createdTask.getTaskId(), TaskStatus.STARTED);
        log.info("Task [id: {}] was successfully launched", createdTask.getTaskId());
        return launchedTask;
    }

}
