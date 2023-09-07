package ru.veselov.transducersmanagingservice.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class TransducerControllerValidationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/transducer";

    @Autowired
    WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {"art", "pressureRange", "outputCode", "model", "code", "pressureType", "accuracy",
            "electricalOutput", "thread", "connector", "pinOut"})
    void shouldReturnValidationErrorWithNullField(String field) {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field(field), null).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(field);
    }

    @ParameterizedTest
    @ValueSource(strings = {"art", "pressureRange", "outputCode", "model", "code", "accuracy",
            "electricalOutput", "thread", "connector", "pinOut"})
    void shouldReturnValidationErrorWithBlankField(String field) {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field(field), "").create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(field);
    }

    @Test
    void shouldReturnValidationErrorWithTooLongOptions() {
        String options = "1".repeat(105);
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field("options"), options).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("options");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForGetById() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/id/notUuid").build())
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("transducerId");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForDelete() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete/notUuid").build())
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("transducerId");
    }

    @Test
    void shouldReturnValidationErrorIfNotUUIDForUpdate() {
        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/update/notUuid").build())
                .bodyValue(Instancio.create(TransducerDto.class))
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("transducerId");
    }

    @Test
    void shouldReturnValidationErrorForSortingParamsNotTransducerField() {
        //passed sort parameter for serial number entity to check validation groups
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam("sort", "number")
                        .build())
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("sort");
    }

}
