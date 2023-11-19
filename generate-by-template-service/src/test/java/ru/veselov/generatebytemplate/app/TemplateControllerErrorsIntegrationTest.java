package ru.veselov.generatebytemplate.app;

import io.minio.MinioClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.app.config.KafkaTestConsumer;
import ru.veselov.generatebytemplate.app.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateControllerErrorsIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/template/";

    @Value("${minio.buckets.template}")
    String templateBucket;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TemplateStorageService templateStorageService;

    @Autowired
    TemplateRepository templateRepository;

    @MockBean
    MinioClient minioClient;

    @MockBean
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    KafkaAdmin kafkaAdmin;

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnNotFoundError() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source")
                        .path("/id/" + TestUtils.TEMPLATE_ID)
                        .build())
                .exchange().expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnNotFoundErrorForUpdate() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + TestUtils.TEMPLATE_ID).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnNotFoundErrorForDelete() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete").path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnErrorIfEntityAlreadyExists() {
        TemplateEntity templateEntity = saveTemplate();
        TemplateDto templateDto = new TemplateDto(
                "filename",
                templateEntity.getPtArt(),
                templateBucket);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_TEMPLATE_EXISTS.toString());
    }

    private TemplateEntity saveTemplate() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestUtils.SAMPLE_FILENAME);
        templateEntity.setTemplateName("801877-filename");
        templateEntity.setBucket(templateBucket);
        templateEntity.setSynced(true);
        templateEntity.setPtArt("801877");
        return templateRepository.save(templateEntity);
    }

}
