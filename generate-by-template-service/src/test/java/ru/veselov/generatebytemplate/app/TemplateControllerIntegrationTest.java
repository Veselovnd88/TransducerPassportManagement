package ru.veselov.generatebytemplate.app;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.app.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateControllerIntegrationTest extends PostgresContainersConfig {

    private static final String URL_PREFIX = "/api/v1/template/";
    private static final String TEMPLATE_NAME = "801877-filename";
    private static final String SAVED_TEMPLATE_NAME = "801877-name";

    public UUID savedTemplateId;

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
    @SneakyThrows
    void shouldGetSourceById() {
        saveTemplateEntity();
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(Mockito.any())).thenReturn(getObjectResponse);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source").
                        path("/id/" + savedTemplateId).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);

        Mockito.verify(minioClient).getObject(getObjectArgsCaptor.capture());
        GetObjectArgs captured = getObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.object()).isEqualTo(TestUtils.SAMPLE_FILENAME);
        Assertions.assertThat(captured.bucket()).isEqualTo(templateBucket);
    }

    @Test
    @SneakyThrows
    void shouldUploadTemplate() {
        TemplateDto templateDto = new TemplateDto("name", TestUtils.ART, templateBucket);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findByName(SAVED_TEMPLATE_NAME);
        Assertions.assertThat(optionalTemplateEntity).isPresent();
        Assertions.assertThat(optionalTemplateEntity.get().getSynced()).isTrue();
        Mockito.verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs captured = putObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.bucket()).isEqualTo(templateBucket);
        Assertions.assertThat(captured.object()).isEqualTo("801877-name.docx");
    }

    @Test
    @SneakyThrows
    void shouldUpdateTemplate() {
        saveTemplateEntity();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + savedTemplateId).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(savedTemplateId);

        Assertions.assertThat(templateEntityOptional).isPresent();
        TemplateEntity templateEntity = templateEntityOptional.get();
        Assertions.assertThat(templateEntity.getEditedAt()).isNotNull();
        Mockito.verify(minioClient).putObject(putObjectArgsCaptor.capture());
        PutObjectArgs captured = putObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.bucket()).isEqualTo(templateBucket);
        Assertions.assertThat(captured.object()).isEqualTo(TestUtils.SAMPLE_FILENAME);
    }

    @Test
    @SneakyThrows
    void shouldDeleteTemplate() {
        saveTemplateEntity();
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete")
                        .path("/id/" + savedTemplateId).build())
                .exchange().expectStatus().isOk();

        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findById(savedTemplateId);

        Assertions.assertThat(optionalTemplateEntity).isNotPresent();
        Mockito.verify(minioClient).removeObject(removeObjectArgsCaptor.capture());
        RemoveObjectArgs captured = removeObjectArgsCaptor.getValue();
        Assertions.assertThat(captured.object()).isEqualTo(TestUtils.SAMPLE_FILENAME);
        Assertions.assertThat(captured.bucket()).isEqualTo(templateBucket);
    }

    @Test
    @SneakyThrows
    void shouldStayUnSyncedFileWasNotSaved() {
        TemplateDto templateDto = new TemplateDto("name", TestUtils.ART, templateBucket);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);
        Mockito.doThrow(CommonMinioException.class).when(minioClient).putObject(Mockito.any());

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findByName(SAVED_TEMPLATE_NAME);
        Assertions.assertThat(optionalTemplateEntity).isPresent();
        Assertions.assertThat(optionalTemplateEntity.get().getSynced()).isFalse();
    }

    @Test
    @SneakyThrows
    void shouldNotUpdateDbIfTemplateNotLoaded() {
        saveTemplateEntity();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES).filename(TestUtils.MULTIPART_FILENAME);
        Mockito.doThrow(CommonMinioException.class).when(minioClient).putObject(Mockito.any());

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + savedTemplateId).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(savedTemplateId);
        Assertions.assertThat(templateEntityOptional).isPresent();
        Assertions.assertThat(templateEntityOptional.get().getEditedAt()).isNull();
    }

    @Test
    @SneakyThrows
    void shouldNotDeleteIfException() {
        saveTemplateEntity();
        Mockito.doThrow(CommonMinioException.class).when(minioClient).removeObject(Mockito.any());

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete").path("/id/" + savedTemplateId).build())
                .exchange().expectStatus().is5xxServerError()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_FILE_STORAGE.toString());

        Optional<TemplateEntity> templateEntityOptional = templateRepository.findById(savedTemplateId);
        Assertions.assertThat(templateEntityOptional).isPresent();
    }

    private void saveTemplateEntity() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestUtils.SAMPLE_FILENAME);
        templateEntity.setTemplateName(TEMPLATE_NAME);
        templateEntity.setBucket(templateBucket);
        templateEntity.setSynced(true);
        templateEntity.setPtArt(TestUtils.ART);
        TemplateEntity save = templateRepository.save(templateEntity);
        savedTemplateId = save.getId();
    }

}
