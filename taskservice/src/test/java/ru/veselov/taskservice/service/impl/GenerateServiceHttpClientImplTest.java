package ru.veselov.taskservice.service.impl;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Body;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import ru.veselov.taskservice.configuration.resttemplate.RestTemplateResponseErrorHandler;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.exception.GenerateServiceException;
import ru.veselov.taskservice.exception.GenerateServiceValidationException;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.util.TestUtils;

import java.util.Collections;

@WireMockTest(httpPort = 30001)
class GenerateServiceHttpClientImplTest {

    private static final String WIREMOCK_URL = "/" + TestUtils.TASK_ID_STR;

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
        WireMock.stubFor(WireMock.post(WIREMOCK_URL).willReturn(WireMock.aResponse().withStatus(202)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();
        Assertions.assertThatNoException().isThrownBy(
                () -> generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME)
        );
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionFor4xxStatus() {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse("1", Collections.emptyList());
        byte[] responseBytes = TestUtils.jsonStringFromObject(validationErrorResponse).getBytes();
        WireMock.stubFor(WireMock.post(WIREMOCK_URL).willReturn(WireMock.aResponse().withStatus(400)
                .withResponseBody(Body.fromJsonBytes(responseBytes))));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        Assertions.assertThatThrownBy(() ->
                        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME))
                .isInstanceOf(GenerateServiceValidationException.class);
    }

    @Test
    void shouldThrowExceptionFor5xxStatus() {
        WireMock.stubFor(WireMock.post(WIREMOCK_URL).willReturn(WireMock.aResponse().withStatus(500)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        Assertions.assertThatThrownBy(() ->
                        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME))
                .isInstanceOf(GenerateServiceException.class);
    }

    @Test
    void shouldThrowExceptionForNotAcceptedStatus() {
        WireMock.stubFor(WireMock.post(WIREMOCK_URL).willReturn(WireMock.aResponse().withStatus(500)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Task task = TestUtils.getTask();

        Assertions.assertThatThrownBy(() ->
                        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, task, TestUtils.USERNAME))
                .isInstanceOf(GenerateServiceException.class);
    }

}
