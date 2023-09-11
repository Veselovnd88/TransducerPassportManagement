package ru.veselov.miniotemplateservice.app;

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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.miniotemplateservice.TestConstants;
import ru.veselov.miniotemplateservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.exception.error.ErrorCode;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateControllerErrorsIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/template/";

    @Value("${minio.bucket-name}")
    String bucketName;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TemplateStorageService templateStorageService;

    @Autowired
    TemplateRepository templateRepository;

    @MockBean
    MinioClient minioClient;

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnNotFoundError() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source")
                        .path("/id/" + TestConstants.TEMPLATE_ID)
                        .build())
                .exchange().expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnNotFoundErrorForUpdate() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestConstants.MULTIPART_FILE, TestConstants.SOURCE_BYTES)
                .filename(TestConstants.MULTIPART_FILENAME);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + TestConstants.TEMPLATE_ID).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnNotFoundErrorForDelete() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete").path("/id/" + TestConstants.TEMPLATE_ID).build())
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnErrorIfEntityAlreadyExists() {
        TemplateEntity templateEntity = saveTemplate();
        TemplateDto templateDto = new TemplateDto(
                "filename",
                templateEntity.getPtArt(),
                bucketName);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestConstants.MULTIPART_FILE, TestConstants.SOURCE_BYTES)
                .filename(TestConstants.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestConstants.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_TEMPLATE_EXISTS.toString());
    }

    private TemplateEntity saveTemplate() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestConstants.SAMPLE_FILENAME);
        templateEntity.setTemplateName("801877-filename");
        templateEntity.setBucket(bucketName);
        templateEntity.setSynced(true);
        templateEntity.setPtArt("801877");
        return templateRepository.save(templateEntity);
    }

}
