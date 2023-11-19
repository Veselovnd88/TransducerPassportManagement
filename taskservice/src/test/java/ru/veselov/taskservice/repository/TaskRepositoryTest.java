package ru.veselov.taskservice.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.veselov.taskservice.util.TestUtils;
import ru.veselov.taskservice.entity.SerialNumberEntity;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryTest extends PostgresContainersConfig {

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
    void shouldSaveTaskWithNewSerialToRepository() {
        TaskEntity taskEntity = createTaskentity();
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(TestUtils.SERIAL_ID, TestUtils.SERIAL);
        taskEntity.addSerialNumber(serialNumberEntity);
        taskRepository.save(taskEntity);

        List<TaskEntity> allTasks = taskRepository.findAll();
        List<SerialNumberEntity> allSerials = serialNumberRepository.findAll();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(allTasks).isNotNull().hasSize(1),
                () -> {
                    Assertions.assertThat(allTasks.get(0)).isNotNull();
                    TaskEntity savedTask = allTasks.get(0);
                    Assertions.assertThat(savedTask.getSerials()).isNotNull().hasSize(1).contains(serialNumberEntity);
                },
                () -> Assertions.assertThat(allSerials).hasSize(1)
        );
    }

    @Test
    void shouldSaveTaskWithExistingSerial() {
        TaskEntity taskEntity = createTaskentity();
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(TestUtils.SERIAL_ID, TestUtils.SERIAL);
        SerialNumberEntity savedSerial = serialNumberRepository.save(serialNumberEntity);
        taskEntity.addSerialNumber(savedSerial);
        taskRepository.save(taskEntity);

        List<TaskEntity> allTasks = taskRepository.findAll();
        List<SerialNumberEntity> allSerials = serialNumberRepository.findAll();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(allTasks).isNotNull().hasSize(1),
                () -> {
                    Assertions.assertThat(allTasks.get(0)).isNotNull();
                    TaskEntity savedTask = allTasks.get(0);
                    Assertions.assertThat(savedTask.getSerials()).isNotNull().hasSize(1).contains(serialNumberEntity);
                },
                () -> Assertions.assertThat(allSerials).hasSize(1)
        );
    }

    @Test
    void shouldGetPerformedTasksFromRepository() {
        TaskEntity performedTask = createTaskentity();
        performedTask.setStatus(TaskStatus.PERFORMED);
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(TestUtils.SERIAL_ID, TestUtils.SERIAL);
        performedTask.addSerialNumber(serialNumberEntity);
        taskRepository.save(performedTask);
        TaskEntity notPerformedTask = createTaskentity();
        taskRepository.save(notPerformedTask);

        List<TaskEntity> performedTasks = taskRepository.findAllPerformedTasksByUsername(TestUtils.USERNAME);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(performedTasks).isNotNull().hasSize(1),
                () -> {
                    assert performedTasks != null;
                    Assertions.assertThat(performedTasks.get(0)).isNotNull();
                    TaskEntity savedTask = performedTasks.get(0);
                    Assertions.assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.PERFORMED);
                }
        );
    }

    @Test
    void shouldGetNotPerformedTasksFromRepository() {
        TaskEntity performedTask = createTaskentity();
        performedTask.setStatus(TaskStatus.PERFORMED);
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(TestUtils.SERIAL_ID, TestUtils.SERIAL);
        performedTask.addSerialNumber(serialNumberEntity);
        taskRepository.save(performedTask);
        TaskEntity notPerformedTask = createTaskentity();
        taskRepository.save(notPerformedTask);

        List<TaskEntity> notPerformedTasks = taskRepository.findAllNotPerformedTasksByUsername(TestUtils.USERNAME);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(notPerformedTasks).isNotNull().hasSize(1),
                () -> {
                    assert notPerformedTasks != null;
                    Assertions.assertThat(notPerformedTasks.get(0)).isNotNull();
                    TaskEntity savedTask = notPerformedTasks.get(0);
                    Assertions.assertThat(savedTask.getStatus()).isEqualTo(TaskStatus.CREATED);
                }
        );
    }

    private TaskEntity createTaskentity() {
        return TaskEntity.builder()
                .username(TestUtils.USERNAME)
                .templateId(TestUtils.TEMPLATE_ID)
                .printDate(TestUtils.PRINT_DATE)
                .build();
    }

}