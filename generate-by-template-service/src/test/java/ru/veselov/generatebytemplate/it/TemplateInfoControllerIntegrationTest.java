package ru.veselov.generatebytemplate.it;

import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.it.config.KafkaTestConsumer;
import ru.veselov.generatebytemplate.it.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateInfoControllerIntegrationTest extends PostgresContainersConfig {

    private static final String URL_PREFIX = "/api/v1/template/info";

    private static final String TEMPLATE_NAME = "templateName";

    private static final String ASC = "asc";

    private static final String PT_ART = "ptArt";

    private static final String DESC = "desc";

    private static final String TEMPLATE_NAME2 = "101855-zzz";

    private static final String TEMPLATE_NAME1 = "801855-abc";

    @Value("${minio.buckets.template}")
    String templateBucket;

    public UUID savedTemplateId;

    public TemplateEntity savedTemplate;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TemplateRepository templateRepository;

    @MockBean
    MinioClient minioClient;

    @MockBean
    KafkaTestConsumer kafkaTestConsumer;

    @BeforeEach
    void init() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestUtils.SAMPLE_FILENAME);
        templateEntity.setTemplateName(TestUtils.SAMPLE_TEMPLATE);
        templateEntity.setBucket(templateBucket);
        templateEntity.setSynced(true);
        templateEntity.setPtArt(TestUtils.ART);
        savedTemplate = templateRepository.save(templateEntity);
        savedTemplateId = savedTemplate.getId();
    }

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnTemplateInfoById() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + savedTemplateId).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(savedTemplateId.toString())
                .jsonPath("$.ptArt").isEqualTo(TestUtils.ART)
                .jsonPath("$.bucket").isEqualTo(templateBucket)
                .jsonPath("$.templateName").isEqualTo(TestUtils.SAMPLE_TEMPLATE)
                .jsonPath("$.filename").isEqualTo(TestUtils.SAMPLE_FILENAME)
                .jsonPath("$.createdAt").isEqualTo(savedTemplate.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))
                .jsonPath("$.editedAt").doesNotExist();
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageDefaultSort() {
        //sort by createdAt, descending
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").queryParam("page", 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TEMPLATE_NAME2);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByTemplateNameDesc() {
        //sort by templateName, descending
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, TEMPLATE_NAME)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TestUtils.SAMPLE_TEMPLATE);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByTemplateNameAsc() {
        //sort by createdAt, asc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, TEMPLATE_NAME)
                        .queryParam(TestUtils.ORDER, ASC)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TEMPLATE_NAME2);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByPtArtNameDesc() {
        //sort by ptArt, desc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, PT_ART)
                        .queryParam(TestUtils.ORDER, DESC)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TestUtils.SAMPLE_TEMPLATE);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByPtArtNameAsc() {
        //sort by ptArt, asc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(TestUtils.PAGE, 0)
                        .queryParam(TestUtils.SORT, PT_ART)
                        .queryParam(TestUtils.ORDER, ASC)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TEMPLATE_NAME2);
    }

    @Test
    void shouldReturnTemplatesWithPartialArt() {
        saveTwoMoreEntities();
        // try to find with 801
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/ptArt").path("/801")
                        .queryParam(TestUtils.PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2);
        //try to find full 801877
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/ptArt").path("/801877")
                        .queryParam(TestUtils.PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(1);
        //try to find 01
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/ptArt").path("/01")
                        .queryParam(TestUtils.PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    void shouldReturnPageExceedsMaxError() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").queryParam(TestUtils.PAGE, 100).build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_MAX_PAGE.toString());
    }

    private void saveTwoMoreEntities() {
        TemplateEntity templateEntity1 = new TemplateEntity();
        templateEntity1.setFilename("801855-abc.docx");
        templateEntity1.setTemplateName(TEMPLATE_NAME1);
        templateEntity1.setBucket(templateBucket);
        templateEntity1.setPtArt("801855");
        templateEntity1.setSynced(true);
        templateRepository.save(templateEntity1);

        TemplateEntity templateEntity2 = new TemplateEntity();
        templateEntity2.setFilename("101855-zzz.docx");
        templateEntity2.setTemplateName(TEMPLATE_NAME2);
        templateEntity2.setBucket(templateBucket);
        templateEntity2.setSynced(true);
        templateEntity2.setPtArt("101855");
        templateRepository.save(templateEntity2);
    }

}
