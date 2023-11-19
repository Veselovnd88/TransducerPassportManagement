package ru.veselov.generatebytemplate.it;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.controller.GeneratePassportController;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.ResultFileService;
import ru.veselov.generatebytemplate.utils.AppConstants;
import ru.veselov.generatebytemplate.utils.TestUrlConstants;
import ru.veselov.generatebytemplate.utils.TestUtils;

@WebMvcTest(controllers = GeneratePassportController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class NoHeaderValidationIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    PassportService passportService;

    @MockBean
    ResultFileService resultFileService;

    @Test
    void shouldReturnValidationErrorForNullUsernameHeader() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.GEN_URL_TASK_ID).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath(TestUtils.JSON_ERROR_CODE).isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD)
                .value(Matchers.endsWith(AppConstants.SERVICE_USERNAME_HEADER));
    }

}
