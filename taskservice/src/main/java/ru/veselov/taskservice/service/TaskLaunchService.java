package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;

public interface TaskLaunchService {

    Task startTask(GeneratePassportsDto generatePassportsDto, String username);

}
