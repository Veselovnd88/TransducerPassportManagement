package ru.veselov.taskservice.service;

import ru.veselov.taskservice.dto.GeneratePassportsDto;

public interface GenerateServiceHttpClient {

    void sendTaskToPerform(GeneratePassportsDto generatePassportsDto, String taskId, String username);

}
