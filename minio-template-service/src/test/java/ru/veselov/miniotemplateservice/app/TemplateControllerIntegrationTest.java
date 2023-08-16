package ru.veselov.miniotemplateservice.app;

import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.miniotemplateservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class TemplateControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/template/";


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

    @BeforeEach
    void init() {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename("801877-filename.docx");
        templateEntity.setTemplateName("filename");
        templateEntity.setBucket(bucketName);
        templateEntity.setPtArt("801877");
        TemplateEntity save = templateRepository.save(templateEntity);
        templateId = save.getId();

    }

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldGetSourceById() {
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(SOURCE_BYTES);
        Mockito.when(minioClient.getObject(ArgumentMatchers.any())).thenReturn(getObjectResponse);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source").path("/" + templateId).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
    }


}
