package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;

public interface GenerateServiceHttpClient {

    void sendTaskToPerform(GeneratePassportsDto generatePassportsDto, Task task, String username);

}
