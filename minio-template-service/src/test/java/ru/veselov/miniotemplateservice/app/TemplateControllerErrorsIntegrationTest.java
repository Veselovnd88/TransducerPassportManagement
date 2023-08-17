package ru.veselov.miniotemplateservice.app;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mockito;
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
import ru.veselov.miniotemplateservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.exception.CommonMinioException;
import ru.veselov.miniotemplateservice.exception.error.ErrorCode;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateControllerErrorsIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/template/";

    public static final String SAMPLE_FILENAME = "801877-filename.docx";

    public static final byte[] SOURCE_BYTES = new byte[]{1, 2, 3};

    public UUID templateId;

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

    @Captor
    ArgumentCaptor<GetObjectArgs> getObjectArgsCaptor;

    @Captor
    ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    @Captor
    ArgumentCaptor<RemoveObjectArgs> removeObjectArgsCaptor;


    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnNotFoundError() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source").path("/" + UUID.randomUUID())
                        .build())
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnValidationErrorForUUIDFieldWhenGetTemplate() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source").path("/" + "not_UUID")
                        .build())
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }


    @Test
    void shouldReturnErrorIfEntityAlreadyExists() {
        TemplateEntity templateEntity = saveTemplate();
        TemplateDto templateDto = new TemplateDto(
                "filename",
                templateEntity.getPtArt(),
                "templates");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.docx");
        multipartBodyBuilder.part("template-info", templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_TEMPLATE_EXISTS.toString());
    }

    @Test
    void shouldReturnValidationErrorNoDocxFile() {
        TemplateEntity templateEntity = saveTemplate();
        TemplateDto templateDto = new TemplateDto(
                "filename",
                templateEntity.getPtArt(),
                "templates");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.pox");
        multipartBodyBuilder.part("template-info", templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("file");
    }

    @Test
    void shouldReturnValidationErrorForWrongTemplateFields() {
        saveTemplate();
        TemplateDto templateDto = new TemplateDto(
                null,
                null,
                null);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.docx");
        multipartBodyBuilder.part("template-info", templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")))
                .jsonPath("$.violations[1].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")))
                .jsonPath("$.violations[2].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")));
    }

    @Test
    @SneakyThrows
    void shouldUpdateTemplate() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.docx");

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").path("/" + templateId).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(templateId);

        Assertions.assertThat(templateEntityOptional).isPresent();
        TemplateEntity templateEntity = templateEntityOptional.get();
        Assertions.assertThat(templateEntity.getEditedAt()).isNotNull();
        Mockito.verify(minioClient, Mockito.times(1)).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs captured = putObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.bucket()).isEqualTo(bucketName);
        Assertions.assertThat(captured.object()).isEqualTo(SAMPLE_FILENAME);
    }

    @Test
    @SneakyThrows
    void shouldDeleteTemplate() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete").path("/" + templateId).build())
                .exchange().expectStatus().isOk();

        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findById(templateId);

        Assertions.assertThat(optionalTemplateEntity).isNotPresent();
        Mockito.verify(minioClient, Mockito.times(1)).removeObject(removeObjectArgsCaptor.capture());
        RemoveObjectArgs captured = removeObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.object()).isEqualTo(SAMPLE_FILENAME);
        Assertions.assertThat(captured.bucket()).isEqualTo(bucketName);
    }

    @Test
    @SneakyThrows
    void shouldRollbackTxIfFileWasNotSaved() {
        TemplateDto templateDto = new TemplateDto("name", "801877", "templates");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.docx");
        multipartBodyBuilder.part("template-info", templateDto);
        Mockito.doThrow(CommonMinioException.class).when(minioClient).putObject(ArgumentMatchers.any());

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findByName("801877-name");
        Assertions.assertThat(optionalTemplateEntity).isNotPresent();
    }

    @Test
    @SneakyThrows
    void shouldRollbackTxIfFileWasNotUpdated() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("file", SOURCE_BYTES).filename("filename.docx");
        Mockito.doThrow(CommonMinioException.class).when(minioClient).putObject(ArgumentMatchers.any());

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").path("/" + templateId).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(templateId);
        Assertions.assertThat(templateEntityOptional).isPresent();
        Assertions.assertThat(templateEntityOptional.get().getEditedAt()).isNull();
    }

    @Test
    @SneakyThrows
    void shouldRollbackTxIfMinioThrewException() {
        Mockito.doThrow(CommonMinioException.class).when(minioClient).removeObject(ArgumentMatchers.any());

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete").path("/" + templateId).build())
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(templateId);
        Assertions.assertThat(templateEntityOptional).isPresent();
    }

    private TemplateEntity saveTemplate() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(SAMPLE_FILENAME);
        templateEntity.setTemplateName("801877-filename");
        templateEntity.setBucket(bucketName);
        templateEntity.setPtArt("801877");
        return templateRepository.save(templateEntity);
    }

}
