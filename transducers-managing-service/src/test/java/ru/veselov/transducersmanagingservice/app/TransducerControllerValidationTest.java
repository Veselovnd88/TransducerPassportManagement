package ru.veselov.transducersmanagingservice.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
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

    @Test
    void shouldReturnValidationErrorWithBlankCodeField() {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field("code"), null).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("code");
    }

    @Test
    void shouldReturnValidationErrorWithBlankPressureTypeField() {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field("pressureType"), null).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("pressureType");
    }

    @Test
    void shouldReturnValidationErrorWithBlankArtField() {
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field("art"), null).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("art");
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

}
