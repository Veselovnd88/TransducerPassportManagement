package ru.veselov.generatebytemplate.app;

import io.minio.MinioClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.veselov.generatebytemplate.TestConstants;
import ru.veselov.generatebytemplate.app.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class TemplateSchedulerIntegrationTest extends PostgresContainersConfig {

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    TemplateStorageService templateStorageService;

    @MockBean
    MinioClient minioClient;

    @MockBean
    KafkaTestConsumer kafkaTestConsumer;

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
    }

    @Test
    void shouldDeleteOldUnSynced() {
        saveUnSyncedTemplateToRepo(LocalDateTime.of(2022, 5, 5, 0, 0));
        Awaitility.await().pollDelay(Duration.ofSeconds(3)).until(() -> true);

        List<TemplateEntity> allAfterDelete = templateRepository.findAll();
        Assertions.assertThat(allAfterDelete).isEmpty();
    }

    @Test
    void shouldNotDeleteFreshUnSynced() {
        saveUnSyncedTemplateToRepo(LocalDateTime.now());
        Awaitility.await().pollDelay(Duration.ofSeconds(3)).until(() -> true);

        List<TemplateEntity> allAfterDelete = templateRepository.findAll();
        Assertions.assertThat(allAfterDelete).hasSize(1);
    }

    @Transactional
    void saveUnSyncedTemplateToRepo(LocalDateTime createdAt) {
        TemplateEntity templateEntity = new TemplateEntity();
        templateEntity.setFilename(TestConstants.SAMPLE_FILENAME);
        templateEntity.setTemplateName("801877-filename");
        templateEntity.setBucket("bucketName");
        templateEntity.setSynced(false);
        templateEntity.setPtArt(TestConstants.ART);
        templateEntity.setCreatedAt(createdAt);
    }

}
