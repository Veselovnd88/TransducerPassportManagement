package ru.veselov.taskservice.it;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.utils.TestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
class SchedulingDeleteTaskServiceIntegrationTest extends PostgresContainersConfig {

    private static final int SCHEDULING_SECONDS = 3;

    private static final String CRON_TIME = "*/%s * * * * *".formatted(SCHEDULING_SECONDS);

    private static final int DAYS_UNTIL_DELETION = 7;

    @Autowired
    private TaskRepository taskRepository;

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
    }


    @DynamicPropertySource
    private static void configureSchedulingProps(DynamicPropertyRegistry registry) {
        registry.add("scheduler.delete-not-started-tasks", () -> CRON_TIME);
        registry.add("scheduler.not-started-task-lifetime", () -> DAYS_UNTIL_DELETION);
    }

    @Test
    void shouldDeleteOldNotStartedTask() {
        TaskEntity oldNotStartedTask = new TaskEntity();
        oldNotStartedTask.setPrintDate(TestUtils.PRINT_DATE);
        oldNotStartedTask.setCreatedAt(LocalDateTime.now().minusDays(DAYS_UNTIL_DELETION + 1));
        oldNotStartedTask.setUsername(TestUtils.USERNAME);
        oldNotStartedTask.setTemplateId(TestUtils.TEMPLATE_ID);
        oldNotStartedTask.setStatus(TaskStatus.CREATED);
        taskRepository.save(oldNotStartedTask);
        Awaitility.await().pollDelay(Duration.ofSeconds(SCHEDULING_SECONDS)).until(() -> true);
        List<TaskEntity> all = taskRepository.findAll();
        Assertions.assertThat(all).isEmpty();
    }


}
