package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;

public interface TaskLaunchService {

    Task launchTask(GeneratePassportsDto generatePassportsDto, String username);

}
