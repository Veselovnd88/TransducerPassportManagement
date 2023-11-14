package ru.veselov.taskservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.dto.SerialNumberDto;
import ru.veselov.taskservice.entity.SerialNumberEntity;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.TaskService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TaskServiceImpl implements TaskService {

    public static final String TASK_NOT_FOUND_LOG_MSG = "Task with such [id: {}] doesn't exists";

    public static final String TASK_NOT_FOUND_EXCEPTION_MSK = "Task with such [id: %s] doesn't exists";

    private final TaskRepository taskRepository;

    private final SerialNumberRepository serialNumberRepository;

    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public Task createTask(GeneratePassportsDto generatePassportsDto, String username) {
        TaskEntity taskEntity = TaskEntity.builder()
                .templateId(UUID.fromString(generatePassportsDto.getTemplateId()))
                .username(username)
                .printDate(generatePassportsDto.getPrintDate())
                .build();
        List<SerialNumberDto> serials = generatePassportsDto.getSerials();
        serials.forEach(s -> {
            UUID serialUid = UUID.fromString(s.getSerialId());
            Optional<SerialNumberEntity> optionalSerialNumber = serialNumberRepository
                    .findSerialNumberById(serialUid);
            if (optionalSerialNumber.isPresent()) {
                taskEntity.addSerialNumber(optionalSerialNumber.get());
            } else {
                taskEntity.addSerialNumber(new SerialNumberEntity(serialUid, s.getSerial()));
            }
        });
        TaskEntity savedTask = taskRepository.save(taskEntity);
        log.info("Task saved with [id: {}] with started=false status", savedTask.getTaskId());
        return taskMapper.toModel(savedTask);
    }

    @Override
    @Transactional
    public Task updateStatusToStarted(UUID taskId) {
        Optional<TaskEntity> optionalTask = taskRepository.findById(taskId);
        TaskEntity taskEntity = optionalTask.orElseThrow(() -> {
            log.error(TASK_NOT_FOUND_LOG_MSG, taskId);
            return new EntityNotFoundException(TASK_NOT_FOUND_EXCEPTION_MSK.formatted(taskId));
        });
        taskEntity.setStarted(true);
        TaskEntity updated = taskRepository.save(taskEntity);
        log.info("Task with [id: {}] updated with started=true status", taskId);
        return taskMapper.toModel(updated);
    }

    @Override
    public Task getTask(String taskId) {
        Optional<TaskEntity> optionalTask = taskRepository.findById(UUID.fromString(taskId));
        TaskEntity taskEntity = optionalTask.orElseThrow(() -> { //example of supplier btw
            log.info(TASK_NOT_FOUND_LOG_MSG, taskId);
            return new EntityNotFoundException(TASK_NOT_FOUND_EXCEPTION_MSK.formatted(taskId));
        }); //if value!=null -> return value, else - throw supplier.get()-> supplier is lambda
        return taskMapper.toModel(taskEntity);
    }

    @Override
    public List<Task> getPerformedTasks(String username) {
        return taskMapper.toModels(taskRepository.findlAllPerformedTasksByUsername(username));
    }

    @Override
    public List<Task> getNotPerformedTasks(String username) {
        return taskMapper.toModels(taskRepository.findAllNotPerformedTasksByUsername(username));
    }

    @Override
    @Transactional
    public void deleteTaskById(UUID taskId) {
        taskRepository.deleteById(taskId);
    }

}
