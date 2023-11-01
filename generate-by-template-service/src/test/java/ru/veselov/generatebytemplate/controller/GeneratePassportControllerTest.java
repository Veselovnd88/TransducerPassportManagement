package ru.veselov.generatebytemplate.controller;

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
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.ResultFileService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class GeneratePassportControllerTest {

    public static final String URL_PREFIX = "/api/v1/generate";

    static WebTestClient webTestClient;

    @Mock
    PassportService passportService;

    @Mock
    ResultFileService resultFileService;

    @InjectMocks
    GeneratePassportController generatePassportController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(generatePassportController).build();
    }

    @Test
    void shouldCallPassportServiceToCreatePassports() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isAccepted();

        Mockito.verify(passportService, Mockito.times(1)).createPassportsPdf(generatePassportsDto);
    }

    @Test
    void shouldGetResultById() {
        String resultUid = UUID.randomUUID().toString();
        ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Mockito.when(resultFileService.getResultFile(resultUid))
                .thenReturn(byteArrayResource);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/result/" + resultUid).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(byteArrayResource.contentLength());

        Mockito.verify(resultFileService, Mockito.times(1)).getResultFile(resultUid);
    }

}
