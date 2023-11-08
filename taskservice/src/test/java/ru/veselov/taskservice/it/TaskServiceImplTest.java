package ru.veselov.taskservice.it;

import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.veselov.taskservice.entity.SerialNumberEntity;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskServiceImplTest extends PostgresContainersConfig {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SerialNumberRepository serialNumberRepository;

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
        serialNumberRepository.deleteAll();
    }

    @Test
    public void shouldSaveTaskWithNewSerialsToRepository() {
        TaskEntity taskEntity = TaskEntity.builder()
                .username("user")
                .templateId(UUID.randomUUID())
                .printDate(LocalDate.now())
                .build();
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(UUID.randomUUID(),
                "1243456789");
        taskEntity.addSerialNumber(serialNumberEntity);
        taskRepository.save(taskEntity);
        List<TaskEntity> allTasks = taskRepository.findAll();
        List<SerialNumberEntity> allSerials = serialNumberRepository.findAll();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(allTasks).hasSize(1),
                () -> Assertions.assertThat(allSerials).hasSize(1)
        );
    }

}