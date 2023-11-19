package ru.veselov.generatebytemplate.it;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.generatebytemplate.utils.TestUrlConstants;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.it.config.KafkaTestConsumer;
import ru.veselov.generatebytemplate.it.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.repository.ResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.ScheduledDeleteService;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.util.Objects;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class CacheTemplateIntegrationTest extends PostgresContainersConfig {

    @Value("${minio.buckets.template}")
    String templateBucket;

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TemplateStorageService templateStorageService;

    @Autowired
    CacheManager cacheManager;

    @MockBean
    TemplateRepository templateRepository;

    @MockBean
    ResultFileRepository resultFileRepository;

    @MockBean
    MinioClient minioClient;

    @MockBean
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    ScheduledDeleteService service;

    @BeforeEach
    void clearCache() {
        cacheManager.getCacheNames().forEach(cacheName ->
                Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
    }

    @Test
    @SneakyThrows
    void shouldGetSourceByIdFromCache() {
        TemplateEntity templateEntity = getTemplateEntity();
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(ArgumentMatchers.any())).thenReturn(getObjectResponse);
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.of(templateEntity));

        //first time repo should be called
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
        Mockito.verify(templateRepository).findById(TestUtils.TEMPLATE_ID);
        Mockito.verify(minioClient).getObject(ArgumentMatchers.any());
        //second time should get from cache
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);

        Mockito.verify(templateRepository).findById(TestUtils.TEMPLATE_ID);
        Mockito.verify(minioClient).getObject(ArgumentMatchers.any());
    }

    @Test
    @SneakyThrows
    void shouldDeleteTemplateFromCacheAfterUpdate() {
        TemplateEntity templateEntity = getTemplateEntity();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(Mockito.any())).thenReturn(getObjectResponse);
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.of(templateEntity));
        //after this request template should be in cache #1 invocation of template repository
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
        //this request should evict cache, #2 invocation for checking if exists, #3 for updating of template repository
        webTestClient.put().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX)
                        .path("/update/upload")
                        .path("/id/" + TestUtils.TEMPLATE_ID).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();
        //this request should get template from storage, #4 invocation of template repository
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
        Mockito.verify(templateRepository, Mockito.times(4)).findById(TestUtils.TEMPLATE_ID);
        Mockito.verify(minioClient, Mockito.times(2)).getObject(Mockito.any());
    }

    @Test
    @SneakyThrows
    void shouldDeleteTemplateFromCacheAfterDelete() {
        TemplateEntity templateEntity = getTemplateEntity();
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(Mockito.any())).thenReturn(getObjectResponse);
        Mockito.when(templateRepository.findById(TestUtils.TEMPLATE_ID)).thenReturn(Optional.of(templateEntity));
        //after this request template should be in cache, #1 invocation of template repository
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
        //should evict cache, #2 invocation for founding, #3 for deletion of template repository
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/delete")
                        .path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk();

        //this request should get template from storage, #4 invocation not from cache
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(TestUrlConstants.TEMPLATE_URL_PREFIX).path("/source").
                        path("/id/" + TestUtils.TEMPLATE_ID).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
        Mockito.verify(templateRepository, Mockito.times(4)).findById(TestUtils.TEMPLATE_ID);
        Mockito.verify(minioClient, Mockito.times(2)).getObject(Mockito.any());
    }

    private TemplateEntity getTemplateEntity() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestUtils.SAMPLE_FILENAME);
        templateEntity.setTemplateName("801877-filename");
        templateEntity.setBucket(templateBucket);
        templateEntity.setSynced(true);
        templateEntity.setPtArt(TestUtils.ART);
        templateEntity.setId(TestUtils.TEMPLATE_ID);
        return templateEntity;
    }

}
