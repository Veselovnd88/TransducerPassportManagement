package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.TaskResultDto;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.events.TaskStatusEventPublisher;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.KafkaListenerTaskResultService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerTaskResultServiceImpl implements KafkaListenerTaskResultService {

    private final TaskRepository taskRepository;

    private final TaskStatusEventPublisher taskStatusEventPublisher;

    @Override
    @KafkaListener(groupId = "task", topics = "task", containerFactory = "listenerFactory")
    public void handleTaskResult(ConsumerRecord<String, TaskResultDto> record) {
        String taskId = record.key();
        TaskResultDto taskResultDto = record.value();
        log.info("Record for [task: {}, result {}] received from kafka", taskId, taskResultDto);
        Optional<TaskEntity> optionalTask = taskRepository.findById(UUID.fromString(taskId));
        if (optionalTask.isPresent()) {
            TaskEntity taskEntity = optionalTask.get();
            taskEntity.setStatus(taskResultDto.taskStatus());
            if (taskResultDto.taskStatus() == TaskStatus.PERFORMED) {
                taskEntity.setFileId(UUID.fromString(taskResultDto.fileId()));
            } else {
                log.error("[Task: {}] was failed with message {}", taskId, taskResultDto.errorMessage());
            }
            taskEntity.setPerformedAt(LocalDateTime.now());
            taskRepository.save(taskEntity);
            log.info("[Task: {}] updated", taskId);
            taskStatusEventPublisher.publishTaskStatus(taskId, EventType.UPDATED);
        } else {
            log.error("[Task: {}] not found for update, smth went wrong", taskId);
            taskStatusEventPublisher.publishTaskStatus(taskId, EventType.ERROR);
        }
    }

}
