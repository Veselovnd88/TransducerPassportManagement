package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;

import java.util.List;

public interface TaskService {

    Task createTask(GeneratePassportsDto generatePassportsDto);

    Task getTask(String taskId);

    List<Task> getPerformedTasks(String username);

    List<Task> getCurrentTasks(String username);

}
