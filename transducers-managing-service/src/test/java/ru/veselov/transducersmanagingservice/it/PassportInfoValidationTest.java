package ru.veselov.transducersmanagingservice.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.controller.PassportInfoController;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.service.PassportInfoService;

@AutoConfigureWebTestClient
@ActiveProfiles("test")
@WebMvcTest(controllers = PassportInfoController.class)
public class PassportInfoValidationTest {

    public static final String URL_PREFIX = "/api/v1/passport";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    PassportInfoService passportInfoService;

    @Test
    void shouldReturnValidationErrorIfNotUUID() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + "notUUID").build())
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("passportId");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForDeleting() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete/id/" + "notUUID").build())
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("passportId");
    }

    @Test
    void shouldReturnValidationErrorForSortingParamsNotPassportField() {
        //passed sort parameter for serial number entity to check validation groups
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam("sort", "inn")
                        .build())
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("sort");
    }


}
