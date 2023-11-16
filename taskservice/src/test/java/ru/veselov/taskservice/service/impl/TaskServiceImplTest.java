package ru.veselov.taskservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.SerialNumberEntity;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.mapper.TaskMapperImpl;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.SubscriptionService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    SerialNumberRepository serialNumberRepository;

    @Mock
    SubscriptionService subscriptionService;

    @InjectMocks
    TaskServiceImpl taskService;

    @Captor
    ArgumentCaptor<TaskEntity> taskEntityCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(taskService, "taskMapper", new TaskMapperImpl(), TaskMapper.class);
    }

    @Test
    void shouldCreateTaskWithNewSerials() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        TaskEntity taskEntityWithUid = TaskEntity.builder()
                .taskId(TestUtils.TASK_ID)
                .username(TestUtils.USERNAME)
                .printDate(TestUtils.PRINT_DATE)
                .build();
        Mockito.when(serialNumberRepository.findSerialNumberById(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(taskRepository.save(taskEntityCaptor.capture())).thenReturn(taskEntityWithUid);

        taskService.createTask(generatePassportsDto, TestUtils.USERNAME);

        TaskEntity captured = taskEntityCaptor.getValue();
        assert captured != null;
        org.junit.jupiter.api.Assertions.assertAll(

                () -> Assertions.assertThat(captured).isNotNull(),
                () -> Assertions.assertThat(captured.getSerials()).isNotNull().hasSize(3)
                        .contains(new SerialNumberEntity(
                                UUID.fromString(TestUtils.SERIAL_DTO_1.getSerialId()),
                                TestUtils.SERIAL_DTO_1.getSerial())),
                () -> Assertions.assertThat(captured.getUsername()).isEqualTo(TestUtils.USERNAME),
                () -> Assertions.assertThat(captured.getPrintDate()).isEqualTo(generatePassportsDto.getPrintDate()),
                () -> Assertions.assertThat(captured.getStatus()).isEqualTo(TaskStatus.CREATED)
        );
    }

    @Test
    void shouldCreateTaskWithMixedSerials() {
        SerialNumberEntity serialNumberEntity2 = new SerialNumberEntity(
                UUID.fromString(TestUtils.SERIAL_DTO_2.getSerialId()),
                TestUtils.SERIAL_DTO_2.getSerial());
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        TaskEntity taskEntityWithUid = createTaskEntityWithUid();
        Mockito.when(serialNumberRepository.findSerialNumberById(Mockito.any()))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(serialNumberEntity2))
                .thenReturn(Optional.empty());

        Mockito.when(taskRepository.save(taskEntityCaptor.capture())).thenReturn(taskEntityWithUid);

        Task task = taskService.createTask(generatePassportsDto, TestUtils.USERNAME);

        TaskEntity captured = taskEntityCaptor.getValue();
        assert captured != null;
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured).isNotNull(),
                () -> Assertions.assertThat(captured.getSerials()).isNotNull().hasSize(3)
                        .contains(
                                new SerialNumberEntity(
                                        UUID.fromString(TestUtils.SERIALS_DTOS.get(0).getSerialId()),
                                        TestUtils.SERIALS_DTOS.get(0).getSerial()),
                                serialNumberEntity2),
                () -> Assertions.assertThat(captured.getUsername()).isEqualTo(TestUtils.USERNAME),
                () -> Assertions.assertThat(captured.getPrintDate()).isEqualTo(generatePassportsDto.getPrintDate()),
                () -> Assertions.assertThat(captured.getStatus()).isEqualTo(TaskStatus.CREATED),
                () -> Assertions.assertThat(task.getTaskId()).isEqualTo(taskEntityWithUid.getTaskId())
        );
    }

    @Test
    void shouldUpdateStatusToStart() {
        TaskEntity notStartedTask = createTaskEntityWithUid();
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID))
                .thenReturn(Optional.of(notStartedTask));

        taskService.updateStatus(TestUtils.TASK_ID, TaskStatus.STARTED);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).save(taskEntityCaptor.capture()),
                () -> {
                    TaskEntity captured = taskEntityCaptor.getValue();
                    Assertions.assertThat(captured.getStatus()).isEqualTo(TaskStatus.STARTED);
                }
        );
    }

    @Test
    void shouldThrowExceptionIfTaskForUpdateNotFound() {
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(
                () -> taskService.updateStatus(TestUtils.TASK_ID, TaskStatus.STARTED)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldGetTaskAndDontCompleteSubscriptions() {
        TaskEntity savedTask = createTaskEntityWithUid();//STARTED STATUS
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(savedTask));
        Task task = taskService.getTask(TestUtils.TASK_ID_STR);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).findById(TestUtils.TASK_ID),
                () -> Assertions.assertThat(task).isNotNull(),
                () -> {
                    assert task != null;
                    Assertions.assertThat(task.getTaskId()).isEqualTo(savedTask.getTaskId());
                },
                () -> Mockito.verifyNoInteractions(subscriptionService)
        );
    }

    @Test
    void shouldGetTaskAndCompleteSubscriptions() {
        TaskEntity savedTask = createTaskEntityWithUid();//STARTED STATUS
        savedTask.setStatus(TaskStatus.PERFORMED);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(savedTask));
        Task task = taskService.getTask(TestUtils.TASK_ID_STR);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).findById(TestUtils.TASK_ID),
                () -> Assertions.assertThat(task).isNotNull(),
                () -> {
                    assert task != null;
                    Assertions.assertThat(task.getTaskId()).isEqualTo(savedTask.getTaskId());
                },
                () -> Mockito.verify(subscriptionService).completeSubscriptionsByTask(TestUtils.TASK_ID_STR)
        );
    }

    @Test
    void shouldThrowExceptionIfTaskNotFoundById() {
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> taskService.getTask(TestUtils.TASK_ID_STR))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void shouldGetPerformedTasks() {
        TaskEntity taskEntity = createTaskEntityWithUid();
        taskEntity.setStatus(TaskStatus.PERFORMED);
        Mockito.when(taskRepository.findAllPerformedTasksByUsername(TestUtils.USERNAME))
                .thenReturn(List.of(taskEntity));

        List<Task> performedTasks = taskService.getPerformedTasks(TestUtils.USERNAME);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(performedTasks).isNotNull().hasSize(1),
                () -> Mockito.verify(taskRepository).findAllPerformedTasksByUsername(TestUtils.USERNAME)
        );
    }

    @Test
    void shouldGetNotPerformedTasks() {
        TaskEntity taskEntity = createTaskEntityWithUid();
        Mockito.when(taskRepository.findAllNotPerformedTasksByUsername(TestUtils.USERNAME))
                .thenReturn(List.of(taskEntity));

        List<Task> notPerformedTasks = taskService.getNotPerformedTasks(TestUtils.USERNAME);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(notPerformedTasks).isNotNull().hasSize(1),
                () -> Mockito.verify(taskRepository).findAllNotPerformedTasksByUsername(TestUtils.USERNAME)
        );
    }

    @Test
    void shouldDeleteTaskById() {
        taskService.deleteTaskById(TestUtils.TASK_ID);

        Mockito.verify(taskRepository).deleteById(TestUtils.TASK_ID);
    }

    private TaskEntity createTaskEntityWithUid() {
        return TaskEntity.builder()
                .taskId(TestUtils.TASK_ID)
                .username(TestUtils.USERNAME)
                .printDate(TestUtils.PRINT_DATE)
                .build();
    }

}
