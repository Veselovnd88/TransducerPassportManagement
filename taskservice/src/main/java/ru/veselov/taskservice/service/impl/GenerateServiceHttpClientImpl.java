package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.exception.GenerateServiceException;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;
import ru.veselov.taskservice.utils.AppConstants;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenerateServiceHttpClientImpl implements GenerateServiceHttpClient {

    @Value("${generate-service.url}")
    private String generateServiceUrl;

    private final RestTemplate restTemplate;

    @Override
    public void sendTaskToPerform(GeneratePassportsDto generatePassportsDto, Task task, String username) {
        HttpEntity<GeneratePassportsDto> postBody = createHttpEntity(generatePassportsDto, username);
        ResponseEntity<Void> answer = restTemplate.exchange(generateServiceUrl + "/" + task.getTaskId(),
                HttpMethod.POST, postBody, Void.class);
        if (!answer.getStatusCode().equals(HttpStatus.ACCEPTED)) {
            log.info("[Task {}] was not sent to generate service for reason: {}", task.getTaskId(), answer.getStatusCode());
            throw new GenerateServiceException(answer.getStatusCode().toString());
        }
        log.info("[Task : {}] successfully sent for perform", task.getTaskId());
    }

    private HttpEntity<GeneratePassportsDto> createHttpEntity(GeneratePassportsDto generatePassportsDto,
                                                              String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.set(AppConstants.SERVICE_USERNAME_HEADER, username);
        return new HttpEntity<>(generatePassportsDto, httpHeaders);
    }

}
