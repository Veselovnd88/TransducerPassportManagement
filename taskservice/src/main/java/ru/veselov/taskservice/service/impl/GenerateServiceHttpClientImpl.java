package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set("Service-username-header", "username");
        HttpEntity<GeneratePassportsDto> postBody = new HttpEntity<>(generatePassportsDto, httpHeaders);
        ResponseEntity<Void> answer = restTemplate.exchange(generateServiceUrl + "/" + task.getTaskId(),
                HttpMethod.POST, postBody, Void.class);
        if (answer.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            log.info("[Task : {}] successfully sent for perform", task.getTaskId());
            return true;
        }
        log.info("[Task {}] was not sent to generate service for reason: ", task.getTaskId());
        throw new RuntimeException();
    }
}
