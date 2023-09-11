package ru.veselov.miniotemplateservice.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.miniotemplateservice.TestConstants;
import ru.veselov.miniotemplateservice.controller.TemplateInfoController;
import ru.veselov.miniotemplateservice.exception.error.ErrorCode;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

@WebMvcTest(controllers = TemplateInfoController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class TemplateInfoControllerValidationTest {

    public static final String URL_PREFIX = "/api/v1/template/info";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    TemplateStorageService templateStorageService;

    @Test
    void shouldReturnErrorWithWrongUUID() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + "notUUID").build())
                .exchange().expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortSortByField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestConstants.PAGE, 0)
                        .queryParam(TestConstants.SORT, "tort")
                        .queryParam(TestConstants.ORDER, "asc")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestConstants.SORT);
    }

    @Test
    void shouldReturnValidationErrorWhenPassNegativePage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestConstants.PAGE, -1).build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestConstants.PAGE);
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortOrderField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestConstants.PAGE, 0)
                        .queryParam(TestConstants.SORT, "ptArt")
                        .queryParam(TestConstants.ORDER, "pasc")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestConstants.ORDER);
    }

}
