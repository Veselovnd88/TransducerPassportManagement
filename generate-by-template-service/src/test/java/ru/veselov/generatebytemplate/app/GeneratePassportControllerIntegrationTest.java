package ru.veselov.generatebytemplate.app;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

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

    @Value("${minio.buckets.template}")
    private String templateBucket;

    public static final String URL_PREFIX = "/api/v1/generate";

    public static final int SIDE_PORT = 30003;

    public static final String sideApi = "http://localhost:%d".formatted(SIDE_PORT);

    public static final String TEMPLATE_ID = UUID.randomUUID().toString();

    public static final byte[] BYTES = new byte[]{1, 2, 3};

    public byte[] DOCX_BYTES;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    KafkaTestConsumer kafkaTestConsumer;

    @Autowired
    TemplateRepository templateRepository;

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

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @DynamicPropertySource
    static void setUpUrls(DynamicPropertyRegistry registry) {
        registry.add("pdf-service.url", () -> sideApi);
    }

    @Test
    @SneakyThrows
    void shouldReturnByteArrayAndSendDataToKafkaTopic() {
        //mock pdf server
        WireMock.stubFor(WireMock.post("/").willReturn(WireMock.aResponse().withStatus(200).withBody(BYTES)));
        TemplateEntity templateEntity = saveTemplateToRepo();
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto(templateEntity.getId().toString());
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(templateBucket)
                .object(templateEntity.getFilename()).build();
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(DOCX_BYTES);
        Mockito.when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_PDF)
                .expectHeader().contentLength(BYTES.length)
                .expectBody(byte[].class);

        Assertions.assertThat(generatePassportsDto).isEqualTo(kafkaTestConsumer.getListenedResult());
    }

    @Test
    @SneakyThrows
    void shouldReturnDocxProcessingError() {
        TemplateEntity templateEntity = saveTemplateToRepo();
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto(templateEntity.getId().toString());
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(templateBucket)
                .object(templateEntity.getFilename()).build();
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(new byte[]{1, 2, 3});
        Mockito.when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_DOC_PROCESSING.toString());
    }

    @Test
    @SneakyThrows
    void shouldReturnPDFProcessingErrorFor400StatusOfPdfService() {
        //mock pdf service
        WireMock.stubFor(WireMock.post("/").willReturn(WireMock.aResponse().withStatus(400)));
        TemplateEntity templateEntity = saveTemplateToRepo();
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto(templateEntity.getId().toString());
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(templateBucket)
                .object(templateEntity.getFilename()).build();
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(DOCX_BYTES);
        Mockito.when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_PDF_PROCESSING.toString());
    }

    @Test
    @SneakyThrows
    void shouldReturnPDFProcessingErrorFor500StatusOfPdfService() {
        //mock pdf service
        WireMock.stubFor(WireMock.post("/").willReturn(WireMock.aResponse().withStatus(500)));
        TemplateEntity templateEntity = saveTemplateToRepo();
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto(templateEntity.getId().toString());
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        GetObjectArgs getObjectArgs = GetObjectArgs.builder().bucket(templateBucket)
                .object(templateEntity.getFilename()).build();
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(DOCX_BYTES);
        Mockito.when(minioClient.getObject(getObjectArgs)).thenReturn(getObjectResponse);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_SERVICE_UNAVAILABLE.toString());
    }

    @Test
    void shouldReturnTemplateNotFoundError() {
        GeneratePassportsDto generatePassportsDto = getGeneratePassportDto(TEMPLATE_ID);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).build())
                .bodyValue(generatePassportsDto).exchange().expectStatus().is4xxClientError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    private GeneratePassportsDto getGeneratePassportDto(String templateId) {
        SerialNumberDto serialNumberDto = new SerialNumberDto("123", UUID.randomUUID().toString());
        SerialNumberDto serialNumberDto2 = new SerialNumberDto("456", UUID.randomUUID().toString());
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .ignore(Select.field("serials"))
                .supply(Select.field(GeneratePassportsDto::getTemplateId), () -> templateId)
                .create();

        generatePassportsDto.setSerials(List.of(serialNumberDto, serialNumberDto2));
        return generatePassportsDto;
    }

    private TemplateEntity saveTemplateToRepo() {
        TemplateEntity templateEntity1 = new TemplateEntity();
        templateEntity1.setFilename("801855-abc.docx");
        templateEntity1.setTemplateName("801855-abc");
        templateEntity1.setBucket(templateBucket);
        templateEntity1.setPtArt("801855");
        templateEntity1.setSynced(true);
        return templateRepository.save(templateEntity1);
    }
}
