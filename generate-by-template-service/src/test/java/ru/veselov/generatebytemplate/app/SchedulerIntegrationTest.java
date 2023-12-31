package ru.veselov.generatebytemplate.app;

import io.minio.MinioClient;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.app.testcontainers.PostgresContainersConfig;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.repository.ResultFileRepository;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
class SchedulerIntegrationTest extends PostgresContainersConfig {

    private static final int SCHEDULING_SECONDS = 3;

    private static final String CRON_TIME = "*/%s * * * * *".formatted(SCHEDULING_SECONDS);

    public static final int DAYS_UNTIL_DELETION = 3;
    private static final String TASK_ID_FIELD = "taskId";
    private static final String SYNCED_FIELD = "synced";
    private static final String TEMPLATE_ENTITY_FIELD = "templateEntity";

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    TemplateStorageService templateStorageService;

    @Autowired
    ResultFileRepository resultFileRepository;

    @MockBean
    MinioClient minioClient;

    @MockBean
    KafkaTestConsumer kafkaTestConsumer;

    @MockBean
    KafkaAdmin kafkaAdmin;

    @AfterEach
    void clear() {
        templateRepository.deleteAll();
        resultFileRepository.deleteAll();
    }

    @DynamicPropertySource
    private static void configureSchedulingProps(DynamicPropertyRegistry registry) {
        registry.add("scheduling.delete-unsync-template:", () -> CRON_TIME);
        registry.add("scheduling.days-until-delete-unsync-template", () -> DAYS_UNTIL_DELETION);
        registry.add("scheduling.delete-unsync-result", () -> CRON_TIME);
        registry.add("scheduling.days-until-delete-unsync-result", () -> DAYS_UNTIL_DELETION);
        registry.add("scheduling.delete-result", () -> CRON_TIME);
        registry.add("scheduling.days-until-delete-result", () -> DAYS_UNTIL_DELETION);
    }

    @Test
    void shouldDeleteOldUnSyncedTemplates() {
        saveUnSyncedTemplateToRepo(LocalDateTime.now().minusDays(DAYS_UNTIL_DELETION + 1));
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);

        List<TemplateEntity> allAfterDelete = templateRepository.findAll();
        Assertions.assertThat(allAfterDelete).isEmpty();
    }

    @Test
    void shouldNotDeleteFreshUnSyncedTemplates() {
        saveUnSyncedTemplateToRepo(LocalDateTime.now());
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);

        List<TemplateEntity> allAfterDelete = templateRepository.findAll();
        Assertions.assertThat(allAfterDelete).hasSize(1);
    }

    @Test
    void shouldDeleteUnSyncedResultFiles() {
        saveResultFileToRepo(LocalDateTime.now().minusDays(DAYS_UNTIL_DELETION + 1), false);
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);
        List<ResultFileEntity> allAfterDelete = resultFileRepository.findAll();
        Assertions.assertThat(allAfterDelete).isEmpty();
    }

    @Test
    void shouldNotDeleteFreshUnSyncResultFile() {
        saveResultFileToRepo(LocalDateTime.now(), false);
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);

        List<ResultFileEntity> allAfterDelete = resultFileRepository.findAll();
        Assertions.assertThat(allAfterDelete).hasSize(1);
    }

    @Test
    void shouldDeleteExpiredResultFile() {
        saveResultFileToRepo(LocalDateTime.now().minusDays(DAYS_UNTIL_DELETION + 1), true);
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);
        List<ResultFileEntity> allAfterDelete = resultFileRepository.findAll();
        Assertions.assertThat(allAfterDelete).isEmpty();
    }

    void saveUnSyncedTemplateToRepo(LocalDateTime createdAt) {
        TemplateEntity templateEntity = Instancio.of(TemplateEntity.class).set(Select.field(SYNCED_FIELD), false)
                .create();
        templateEntity.setCreatedAt(createdAt);
        templateRepository.save(templateEntity);
    }

    void saveResultFileToRepo(LocalDateTime createdAt, Boolean sync) {
        TemplateEntity templateEntity = Instancio.of(TemplateEntity.class).set(Select.field(SYNCED_FIELD), true).create();
        TemplateEntity savedTemplate = templateRepository.saveAndFlush(templateEntity);
        ResultFileEntity resultFileEntity = Instancio.of(ResultFileEntity.class)
                .set(Select.field(TEMPLATE_ENTITY_FIELD), savedTemplate)
                .set(Select.field(SYNCED_FIELD), sync)
                .set(Select.field(TASK_ID_FIELD), UUID.fromString(TestUtils.TASK_ID_STR))
                .create();
        resultFileEntity.setCreatedAt(createdAt);
        resultFileRepository.save(resultFileEntity);
    }

}
