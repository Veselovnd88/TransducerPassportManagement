package ru.veselov.miniotemplateservice.app;

import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.miniotemplateservice.TestConstants;
import ru.veselov.miniotemplateservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.exception.error.ErrorCode;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class TemplateInfoControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/template/info";

    public static final String PAGE = "page";

    public static final String SORT = "sort";

    public static final String ORDER = "order";

    @Value("${minio.bucket-name}")
    String bucketName;

    public UUID templateId;

    public TemplateEntity savedTemplate;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TemplateRepository templateRepository;

    @MockBean
    MinioClient minioClient;

    @BeforeEach
    void init() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestConstants.SAMPLE_FILENAME);
        templateEntity.setTemplateName(TestConstants.SAMPLE_TEMPLATE);
        templateEntity.setBucket(bucketName);
        templateEntity.setSynced(true);
        templateEntity.setPtArt(TestConstants.ART);
        savedTemplate = templateRepository.save(templateEntity);
        templateId = savedTemplate.getId();
    }

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnTemplateInfoById() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/" + templateId).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(templateId.toString())
                .jsonPath("$.ptArt").isEqualTo(TestConstants.ART)
                .jsonPath("$.bucket").isEqualTo(bucketName)
                .jsonPath("$.templateName").isEqualTo(TestConstants.SAMPLE_TEMPLATE)
                .jsonPath("$.filename").isEqualTo(TestConstants.SAMPLE_FILENAME)
                .jsonPath("$.createdAt").isEqualTo(savedTemplate.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-mm-dd HH:mm:ss")))
                .jsonPath("$.editedAt").doesNotExist();
    }

    @Test
    void shouldReturnErrorWithWrongUUID() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/" + "notUUID").build())
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageDefaultSort() {
        //sort by createdAt, descending
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").queryParam("page", 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo("101855-zzz");
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByTemplateNameDesc() {
        //sort by templateName, descending
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "templateName")
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TestConstants.SAMPLE_TEMPLATE);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByTemplateNameAsc() {
        //sort by createdAt, asc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "templateName")
                        .queryParam(ORDER, "asc")
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo("101855-zzz");
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByPtArtNameDesc() {
        //sort by ptArt, desc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "ptArt")
                        .queryParam(ORDER, "desc")
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo(TestConstants.SAMPLE_TEMPLATE);
    }

    @Test
    void shouldReturnAllTemplatedWithFirstPageByPtArtNameAsc() {
        //sort by ptArt, asc
        saveTwoMoreEntities();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "ptArt")
                        .queryParam(ORDER, "asc")
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3)
                .jsonPath("$[0].templateName").isEqualTo("101855-zzz");
    }

    @Test
    void shouldReturnTemplatesWithPartialArt() {
        saveTwoMoreEntities();
        // try to find with 801
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").path("/801")
                        .queryParam(PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2);
        //try to find full 801877
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").path("/801877")
                        .queryParam(PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(1);
        //try to find 01
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").path("/01")
                        .queryParam(PAGE, 0).build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    void shouldReturnValidationErrorWhenPassNegativePage() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").queryParam(PAGE, -1).build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(PAGE);
    }

    @Test
    void shouldReturnPageExceedsMaxError() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").queryParam(PAGE, 100).build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_MAX_PAGE.toString());
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortSortByField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "tort")
                        .queryParam(ORDER, "asc")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(SORT);
    }

    @Test
    void shouldReturnValidationErrorPassingNotCorrectSortOrderField() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam(PAGE, 0)
                        .queryParam(SORT, "ptArt")
                        .queryParam(ORDER, "pasc")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo(ORDER);
    }

    private void saveTwoMoreEntities() {
        TemplateEntity templateEntity1 = new TemplateEntity();
        templateEntity1.setFilename("801855-abc.docx");
        templateEntity1.setTemplateName("801855-abc");
        templateEntity1.setBucket(bucketName);
        templateEntity1.setPtArt("801855");
        templateEntity1.setSynced(true);
        templateRepository.save(templateEntity1);

        TemplateEntity templateEntity2 = new TemplateEntity();
        templateEntity2.setFilename("101855-zzz.docx");
        templateEntity2.setTemplateName("101855-zzz");
        templateEntity2.setBucket(bucketName);
        templateEntity2.setSynced(true);
        templateEntity2.setPtArt("101855");
        templateRepository.save(templateEntity2);
    }


}
