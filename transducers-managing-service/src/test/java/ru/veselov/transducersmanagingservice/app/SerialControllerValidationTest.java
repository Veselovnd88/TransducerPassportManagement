package ru.veselov.transducersmanagingservice.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.controller.SerialNumberController;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;

@AutoConfigureWebTestClient
@WebMvcTest(controllers = SerialNumberController.class)
@ActiveProfiles("test")
class SerialControllerValidationTest {

    private static final String URL_PREFIX = "/api/v1/serials";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SerialNumberService serialNumberService;

    @Test
    void shouldReturnValidationErrorIfNotUUIDForGetById() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + "notUUID")
                        .build()).exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("serialId");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForDeleting() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/" + "notUUID")
                        .build()).exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("serialId");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForCustomerId() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/all/dates/art/801877/customer/NotUUID")
                        .build()).exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("customerId");
    }

}
