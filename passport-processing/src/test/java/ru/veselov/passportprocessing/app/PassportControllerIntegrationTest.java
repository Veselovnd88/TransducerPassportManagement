package ru.veselov.passportprocessing.app;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.passportprocessing.app.testcontainers.PostgresContainersConfig;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.entity.PassportEntity;
import ru.veselov.passportprocessing.exception.error.ErrorCode;
import ru.veselov.passportprocessing.repository.PassportRepository;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
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

    @Autowired
    CacheManager cacheManager;

    @Autowired
    PassportRepository passportRepository;

    @BeforeEach
    @SneakyThrows
    void init() {
        WireMock.configureFor("localhost", SIDE_PORT);
        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("file.docx")) {
            assert templateInputStream != null;
            DOCX_BYTES = templateInputStream.readAllBytes();
        }
    }

    @AfterEach
    void clear() {
        passportRepository.deleteAll();
        Objects.requireNonNull(cacheManager.getCache("templates")).clear();
    }

    @DynamicPropertySource
    static void setUpUrls(DynamicPropertyRegistry registry) {
        registry.add("pdf-service.url", () -> sideApi);
        registry.add("template-storage.url", () -> sideApi);
    }

    @Test
    @SneakyThrows
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
        Thread.sleep(100);//TODO replace with awaitility
        List<PassportEntity> savedPassports = passportRepository.findAll();
        Assertions.assertThat(savedPassports).hasSize(generatePassportsDto.getSerials().size());
        Assertions.assertThat(savedPassports.get(0).getPtArt()).isEqualTo(generatePassportsDto.getPtArt());
        Assertions.assertThat(savedPassports.get(0).getTemplateId())
                .isEqualTo(UUID.fromString(generatePassportsDto.getTemplateId()));
        Assertions.assertThat(savedPassports.get(0).getSerial()).isIn(generatePassportsDto.getSerials());
        Assertions.assertThat(savedPassports.get(0).getId()).isNotNull();
        Assertions.assertThat(savedPassports.get(0).getCreatedAt()).isNotNull();
        Assertions.assertThat(savedPassports.get(0).getPrintDate()).isNotNull();
        //TODO check why there is no entities during check
    }

    @Test
    void shouldReturnDocxProcessingError() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(new byte[]{1, 2, 3})));
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_DOC_PROCESSING.toString());
    }

    @Test
    void shouldReturnPDFProcessingError() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(DOCX_BYTES)));
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(400)));
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_PDF_PROCESSING.toString());
    }

    @Test
    void shouldReturnPDFProcessingErrorFor500StatusPdfError() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(DOCX_BYTES)));
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(500)));
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_SERVICE_UNAVAILABLE.toString());
    }

    @Test
    void shouldReturnTemplateNotFoundErrorIfTemplateStorageStatus404() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())));
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnServerUnavailableErrorIfStorageServiceStatus500() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_SERVICE_UNAVAILABLE.toString());
    }


}
