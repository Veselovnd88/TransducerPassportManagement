package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
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
    public Task startTask(GeneratePassportsDto generatePassportsDto, String username) {
        Task createdTask = taskService.createTask(generatePassportsDto, username);
        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, createdTask, username);
        Task startedTask = taskService.updateStatusToStart(createdTask.getTaskId());
        log.info("Task [id: {}] was successfully started", createdTask.getTaskId());
        return startedTask;
        //TODO implement scheduled service for deleting not started tasks
    }

}
