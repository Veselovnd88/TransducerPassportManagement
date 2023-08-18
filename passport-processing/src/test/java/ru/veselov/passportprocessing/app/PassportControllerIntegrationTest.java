package ru.veselov.passportprocessing.app;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.passportprocessing.app.testcontainers.PostgresContainersConfig;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;

import java.io.InputStream;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 30003)
@ActiveProfiles("test")
public class PassportControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/passport";

    public static final int SIDE_PORT = 30003;

    public static final String sideApi = "http://localhost:%d".formatted(SIDE_PORT);

    public static final String TEMPLATE_ID = UUID.randomUUID().toString();

    public static final byte[] BYTES = new byte[]{1, 2, 3};

    public static final String templatePath = "source/" + TEMPLATE_ID;

    public byte[] DOCX_BYTES;

    @Autowired
    WebTestClient webTestClient;

    @BeforeEach
    @SneakyThrows
    void init() {
        WireMock.configureFor("localhost", SIDE_PORT);
        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("file.docx");) {
            assert templateInputStream != null;
            DOCX_BYTES = templateInputStream.readAllBytes();
        }
    }

    @DynamicPropertySource
    static void setUpUrls(DynamicPropertyRegistry registry) {
        registry.add("pdf-service.url", () -> sideApi);
        registry.add("template-storage.url", () -> sideApi);
    }

    @Test
    void shouldReturnByteArrayAndSaveGeneratedDataToDB() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(DOCX_BYTES)));
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(200).withBody(BYTES)));

        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(BYTES.length)
                .expectBody(byte[].class);
    }

}

