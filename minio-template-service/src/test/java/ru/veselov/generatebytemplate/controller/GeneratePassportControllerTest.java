package ru.veselov.generatebytemplate.controller;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.service.PassportService;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeneratePassportControllerTest {

    public static final String URL_PREFIX = "/api/v1/passport";

    public static final byte[] BYTES_OUT = new byte[]{1, 2, 4};

    static WebTestClient webTestClient;

    @Mock
    PassportService passportService;

    @InjectMocks
    GeneratePassportController generatePassportController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(generatePassportController).build();
    }

    @Test
    void shouldCallPassportService() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> UUID.randomUUID().toString())
                .create();
        Mockito.when(passportService.createPassportsPdf(generatePassportsDto)).thenReturn(BYTES_OUT);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(BYTES_OUT.length)
                .expectBody(byte[].class);
    }

    @Test
    void shouldReturnBadRequestStatusIfNotCorrectUUID() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> "not UUID")
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestStatusIfEmptyList() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getSerials), Collections::emptyList)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestStatusIfTemplateIdIsNull() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> null)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestStatusIfDateIsNull() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getPrintDate), () -> null)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isBadRequest();
    }

}
