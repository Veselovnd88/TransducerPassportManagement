package ru.veselov.generatebytemplate.app;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.app.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.SerialNumberDto;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.PassportTemplateService;
import ru.veselov.generatebytemplate.service.TemplateMinioService;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 30003)
@Import({WebClientTestConfiguration.class, KafkaConsumerTestConfiguration.class})
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@DirtiesContext
@ActiveProfiles("test")
public class GeneratePassportControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/passport";

    public static final int SIDE_PORT = 30003;

    public static final String sideApi = "http://localhost:%d".formatted(SIDE_PORT);

    public static final String TEMPLATE_ID = UUID.randomUUID().toString();

    public static final byte[] BYTES = new byte[]{1, 2, 3};

    public static final String templatePath = "source/id/" + TEMPLATE_ID;

    public byte[] DOCX_BYTES;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    TemplateMinioService templateMinioService;

    @MockBean
    TemplateStorageService templateStorageService;

    @MockBean
    PassportTemplateService passportTemplateService;

    @MockBean
    MinioClient minioClient;

    @BeforeEach
    @SneakyThrows
    void init() {
        WireMock.configureFor("localhost", SIDE_PORT);
        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream("file.docx")) {
            assert templateInputStream != null;
            DOCX_BYTES = templateInputStream.readAllBytes();
        }
    }

    @DynamicPropertySource
    static void setUpUrls(DynamicPropertyRegistry registry) {
        registry.add("pdf-service.url", () -> sideApi);
    }

    @Test
    @SneakyThrows
    void shouldReturnByteArrayAndSendDataToKafkaTopic() {
        Mockito.when(passportTemplateService.getTemplate(TEMPLATE_ID))
                .thenReturn(new ByteArrayResource(DOCX_BYTES));
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(200).withBody(BYTES)));
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(BYTES.length)
                .expectBody(byte[].class);

        Assertions.assertThat(generatePassportsDto).isEqualTo(kafkaTestConsumer.getListenedResult());
    }

    @Test
    void shouldReturnDocxProcessingError() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(new byte[]{1, 2, 3})));
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

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
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

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
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_SERVICE_UNAVAILABLE.toString());
    }

    @Test
    void shouldReturnTemplateNotFoundErrorIfTemplateStorageStatus404() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())));
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnServerUnavailableErrorIfStorageServiceStatus500() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_SERVICE_UNAVAILABLE.toString());
    }

    @Test
    void shouldReturnTemplateStorageErrorIfStorageServiceReturnNullBytesArray() {
        WireMock.stubFor(WireMock.get("/" + templatePath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(new byte[]{})));
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/generate").build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_DOC_PROCESSING.toString());
    }

    private GeneratePassportsDto getGeneratePassportDto() {
        SerialNumberDto serialNumberDto = new SerialNumberDto("123", UUID.randomUUID().toString());
        SerialNumberDto serialNumberDto2 = new SerialNumberDto("456", UUID.randomUUID().toString());
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .ignore(Select.field("serials"))
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> TEMPLATE_ID)
                .create();

        generatePassportsDto.setSerials(List.of(serialNumberDto, serialNumberDto2));
        return generatePassportsDto;
    }

}
