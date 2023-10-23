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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.service.GeneratedResultFileService;
import ru.veselov.generatebytemplate.service.PassportService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeneratePassportControllerTest {

    public static final String URL_PREFIX = "/api/v1/generate";

    static WebTestClient webTestClient;

    @Mock
    PassportService passportService;

    @Mock
    GeneratedResultFileService generatedResultFileService;

    @InjectMocks
    GeneratePassportController generatePassportController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(generatePassportController).build();
    }

    @Test
    void shouldCallPassportServiceToCreatePassports() {
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> UUID.randomUUID().toString())
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isAccepted();

        Mockito.verify(passportService, Mockito.times(1)).createPassportsPdf(generatePassportsDto);
    }

    @Test
    void shouldGetResultById() {
        String resultUid = UUID.randomUUID().toString();
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(generatedResultFileService.getResultFile(resultUid))
                .thenReturn(byteArrayResource);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/result/" + resultUid).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(byteArrayResource.contentLength());

        Mockito.verify(generatedResultFileService, Mockito.times(1)).getResultFile(resultUid);
    }


}
