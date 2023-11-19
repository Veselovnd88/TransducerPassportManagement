package ru.veselov.generatebytemplate.it;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.it.config.WebTestClientTestConfiguration;
import ru.veselov.generatebytemplate.controller.GeneratePassportController;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.ResultFileService;
import ru.veselov.generatebytemplate.utils.AppConstants;
import ru.veselov.generatebytemplate.utils.TestUrlConstants;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.utils.argumentprovider.WrongAndNullUUIDArgumentProvider;

import java.time.LocalDate;
import java.util.Collections;

@WebMvcTest(controllers = GeneratePassportController.class)
@AutoConfigureWebTestClient
@Import(WebTestClientTestConfiguration.class)
@ActiveProfiles("test")
public class GeneratePassportControllerValidationIntegrationTest {

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

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.GEN_URL_TASK_ID).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath(TestUtils.JSON_ERROR_CODE).isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).isEqualTo("serials");
    }

    @ParameterizedTest
    @ArgumentsSource(WrongAndNullUUIDArgumentProvider.class)
    void shouldReturnValidationErrorForNullAndNotUUIDTemplateId(String templateId) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setTemplateId(templateId);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.GEN_URL_TASK_ID).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath(TestUtils.JSON_ERROR_CODE).isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).isEqualTo("templateId");
    }

    @ParameterizedTest
    @NullSource
    void shouldReturnValidationErrorForEmptyDate(LocalDate date) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        generatePassportsDto.setPrintDate(date);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.GEN_URL_TASK_ID).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath(TestUtils.JSON_ERROR_CODE).isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).isEqualTo("printDate");
    }

    @ParameterizedTest
    @EmptySource
    void shouldReturnValidationErrorForEmptyUsername(String username) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.GEN_URL_TASK_ID).build())
                .header(AppConstants.SERVICE_USERNAME_HEADER, username)
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath(TestUtils.JSON_ERROR_CODE).isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).isEqualTo(TestUtils.USERNAME);
    }

}
