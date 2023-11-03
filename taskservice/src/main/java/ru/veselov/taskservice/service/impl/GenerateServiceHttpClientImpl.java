package ru.veselov.taskservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;

@Service
@Slf4j
public class GenerateServiceHttpClientImpl implements GenerateServiceHttpClient {
    @Override
    public void sendTaskToPerform(GeneratePassportsDto generatePassportsDto, String taskId, String username) {
        log.info("Send [task : {}] for perform", taskId);//TODO implement
    }
}
