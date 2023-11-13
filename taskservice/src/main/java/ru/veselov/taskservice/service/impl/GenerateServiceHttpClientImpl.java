package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateServiceHttpClientImpl implements GenerateServiceHttpClient {

    @Value("${generate-service.url}")
    private String generateServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public boolean sendTaskToPerform(GeneratePassportsDto generatePassportsDto, Task task) {
       // restTemplate.exchange(generateServiceUrl, HttpMethod.POST)
        log.info("Send [task : {}] for perform", task.getTaskId());//TODO implement
        return true;
    }
}
