package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.model.Task;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    Task createTask(GeneratePassportsDto generatePassportsDto, String username);

    Task updateStatus(UUID taskId, TaskStatus status);

    Task getTask(String taskId);

    List<Task> getPerformedTasks(String username);

    List<Task> getNotPerformedTasks(String username);

    void deleteTaskById(UUID taskId);

}
