package ru.veselov.taskservice.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.taskservice.dto.TaskResultDto;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.event.EventType;
import ru.veselov.taskservice.event.TaskStatusEventPublisher;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.util.TestUtils;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class KafkaListenerTaskResultServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskStatusEventPublisher taskStatusEventPublisher;

    @InjectMocks
    KafkaListenerTaskResultServiceImpl kafkaListenerTaskResultService;

    @Captor
    ArgumentCaptor<TaskEntity> taskArgumentCaptor;

    @Test
    void shouldHandleSuccessResultAndUpdateTask() {
        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID_STR, null, TaskStatus.PERFORMED);
        ConsumerRecord<String, TaskResultDto> resultDtoConsumerRecord = new ConsumerRecord<>(
                "task", 0, 100, TestUtils.TASK_ID_STR, taskResultDto);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setStatus(TaskStatus.STARTED);
        taskEntity.setTaskId(TestUtils.TASK_ID);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(taskEntity));

        kafkaListenerTaskResultService.handleTaskResult(resultDtoConsumerRecord);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).save(taskArgumentCaptor.capture()),
                () -> Mockito.verify(taskStatusEventPublisher)
                        .publishTaskStatus(TestUtils.TASK_ID_STR, EventType.UPDATED),
                () -> {
                    TaskEntity captured = taskArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getStatus()).isEqualTo(TaskStatus.PERFORMED);
                    Assertions.assertThat(captured.getFileId()).isEqualTo(TestUtils.FILE_ID);
                }
        );
    }

    @Test
    void shouldHandleErrorResultAndUpdateTask() {
        TaskResultDto taskResultDto = new TaskResultDto(null, "error message", TaskStatus.FAILED);
        ConsumerRecord<String, TaskResultDto> resultDtoConsumerRecord = new ConsumerRecord<>(
                "task", 0, 100, TestUtils.TASK_ID_STR, taskResultDto);
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setStatus(TaskStatus.STARTED);
        taskEntity.setTaskId(TestUtils.TASK_ID);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.of(taskEntity));

        kafkaListenerTaskResultService.handleTaskResult(resultDtoConsumerRecord);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository).save(taskArgumentCaptor.capture()),
                () -> Mockito.verify(taskStatusEventPublisher)
                        .publishTaskStatus(TestUtils.TASK_ID_STR, EventType.UPDATED),
                () -> {
                    TaskEntity captured = taskArgumentCaptor.getValue();
                    Assertions.assertThat(captured.getStatus()).isEqualTo(TaskStatus.FAILED);
                    Assertions.assertThat(captured.getFileId()).isNull();
                }
        );
    }

    @Test
    void shouldPublishErrorEventIfTaskNotFound() {
        TaskResultDto taskResultDto = new TaskResultDto(null, "error message", TaskStatus.FAILED);
        ConsumerRecord<String, TaskResultDto> resultDtoConsumerRecord = new ConsumerRecord<>(
                "task", 0, 100, TestUtils.TASK_ID_STR, taskResultDto);
        Mockito.when(taskRepository.findById(TestUtils.TASK_ID)).thenReturn(Optional.empty());

        kafkaListenerTaskResultService.handleTaskResult(resultDtoConsumerRecord);
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Mockito.verify(taskRepository, Mockito.never()).save(Mockito.any()),
                () -> Mockito.verify(taskStatusEventPublisher)
                        .publishTaskStatus(TestUtils.TASK_ID_STR, EventType.ERROR)
        );
    }


}