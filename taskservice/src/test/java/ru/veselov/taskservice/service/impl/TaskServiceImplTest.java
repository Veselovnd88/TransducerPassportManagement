package ru.veselov.taskservice.service.impl;

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
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.mapper.TaskMapperImpl;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    SerialNumberRepository serialNumberRepository;

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
                () -> Assertions.assertThat(captured.getPerformed()).isFalse(),
                () -> Assertions.assertThat(captured.getStarted()).isFalse()
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
                () -> Assertions.assertThat(captured.getPerformed()).isFalse(),
                () -> Assertions.assertThat(captured.getStarted()).isFalse(),
                () -> Assertions.assertThat(task.getUid()).isEqualTo(taskEntityWithUid.getTaskId())
        );
    }

    @Test
    void updateStatusToStart() {
        TaskEntity notStartedTask = createTaskEntityWithUid();
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID))
                .thenReturn(Optional.of(notStartedTask));

        taskService.updateStatusToStart(TestUtils.TASK_ID);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).save(taskEntityCaptor.capture()),
                () -> {
                    TaskEntity captured = taskEntityCaptor.getValue();
                    Assertions.assertThat(captured.getStarted()).isTrue();
                }
        );
    }

    @Test
    void getTask() {
    }

    @Test
    void getPerformedTasks() {
    }

    @Test
    void getCurrentTasks() {
    }

    private TaskEntity createTaskEntityWithUid() {
        return TaskEntity.builder()
                .taskId(TestUtils.TASK_ID)
                .username(TestUtils.USERNAME)
                .printDate(TestUtils.PRINT_DATE)
                .build();
    }
}