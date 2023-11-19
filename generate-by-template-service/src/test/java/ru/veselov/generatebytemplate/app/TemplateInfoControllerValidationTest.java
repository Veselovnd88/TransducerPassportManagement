package ru.veselov.generatebytemplate.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.controller.TemplateInfoController;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

@WebMvcTest(controllers = TemplateInfoController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class TemplateInfoControllerValidationTest {

    public static final String URL_PREFIX = "/api/v1/template/info";
    public static final String TEMPLATE_ID_FIELD = "templateId";
    public static final String ASC = "asc";
    public static final String PT_ART = "ptArt";

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
                .jsonPath("$.violations[0].fieldName").isEqualTo(TEMPLATE_ID_FIELD);
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortSortByField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, "tort")
                        .queryParam(TestUtils.ORDER, ASC)
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestUtils.SORT);
    }

    @Test
    void shouldReturnValidationErrorWhenPassNegativePage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, -1).build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestUtils.PAGE);
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortOrderField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, PT_ART)
                        .queryParam(TestUtils.ORDER, "pasc")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(TestUtils.ORDER);
    }

}
