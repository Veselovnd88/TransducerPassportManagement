package ru.veselov.taskservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.SerializationUtils;
import org.springframework.web.client.RestTemplate;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.configuration.RestTemplateResponseErrorHandler;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.exception.GenerateServiceException;
import ru.veselov.taskservice.exception.GenerateServiceValidationException;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;
import ru.veselov.taskservice.model.Task;

import java.util.Collections;

@WireMockTest(httpPort = 30001)
class GenerateServiceHttpClientImplTest {

    GenerateServiceHttpClientImpl generateServiceHttpClient;

    @BeforeEach
    void init() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler(new Jackson2ObjectMapperBuilder().build()));
        int PORT = 30001;
        String generateServiceUrl = "http://localhost:%d".formatted(PORT);
        generateServiceHttpClient = new GenerateServiceHttpClientImpl(restTemplate);
        ReflectionTestUtils.setField(generateServiceHttpClient, "generateServiceUrl", generateServiceUrl, String.class);
        WireMock.configureFor("localhost", PORT);
    }

    @Test
    void shouldSendTaskToPerform() {
        WireMock.stubFor(WireMock.post("/" + TestUtils.TASK_ID_STR).willReturn(WireMock.aResponse().withStatus(202)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME);
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionFor4xxStatus() {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse("1", Collections.emptyList());
        byte[] responseBytes = jsonStringFromResponseObject(validationErrorResponse).getBytes();
        WireMock.stubFor(WireMock.post("/" + TestUtils.TASK_ID_STR).willReturn(WireMock.aResponse().withStatus(400)
                .withResponseBody(Body.fromJsonBytes(responseBytes))));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        Assertions.assertThatThrownBy(() ->
                        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME))
                .isInstanceOf(GenerateServiceValidationException.class);
    }

    @Test
    void shouldThrowExceptionFor5xxStatus() {
        WireMock.stubFor(WireMock.post("/" + TestUtils.TASK_ID_STR).willReturn(WireMock.aResponse().withStatus(500)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        Assertions.assertThatThrownBy(() ->
                        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME))
                .isInstanceOf(GenerateServiceException.class);
    }

    private String jsonStringFromResponseObject(ValidationErrorResponse validationErrorResponse) throws JsonProcessingException {
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        return objectMapper.writeValueAsString(validationErrorResponse);
    }

}
