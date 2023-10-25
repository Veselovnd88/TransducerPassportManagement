package ru.veselov.generatebytemplate.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.controller.GeneratePassportController;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.ResultFileService;

import java.util.Collections;

@WebMvcTest(controllers = GeneratePassportController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class GeneratePassportControllerValidationIntegrationTest {

    public static final String URL_PREFIX = "/api/v1/generate";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    PassportService passportService;

    @MockBean
    ResultFileService resultFileService;

    @Test
    void shouldReturnValidationErrorForEmptyList() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setSerials(Collections.emptyList());

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("serials");
    }

    @Test
    void shouldReturnValidationErrorForEmptyTemplateId() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setTemplateId(null);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnValidationErrorForNotUUIDTemplateId() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setTemplateId("notUUID");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnValidationErrorForNotUUIDTaskId() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setTaskId("notUUID");

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("taskId");
    }

    @Test
    void shouldReturnValidationErrorForEmptyDate() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setPrintDate(null);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("printDate");
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldReturnValidationErrorForEmptyOrNullUsername(String username) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setUsername(username);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("username");
    }

}
