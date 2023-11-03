package ru.veselov.taskservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.mapper.TaskMapper;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.GenerateServiceHttpClient;
import ru.veselov.taskservice.service.TaskService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final GenerateServiceHttpClient generateServiceHttpClient;

    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public Task createTask(GeneratePassportsDto generatePassportsDto, String username) {
        TaskEntity taskEntity = TaskEntity.builder()
                .isPerformed(false)
                .username(username)
                .build();
        TaskEntity savedTask = taskRepository.save(taskEntity);
        log.info("Task saved with [id: {}]", savedTask.getTaskId());
        generateServiceHttpClient.sendTaskToPerform(generatePassportsDto, savedTask.getTaskId().toString(), username);
        return taskMapper.toModel(savedTask);
    }

    @Override
    public Task getTask(String taskId) {
        Optional<TaskEntity> optionalTask = taskRepository.findById(UUID.fromString(taskId));
        TaskEntity taskEntity = optionalTask.orElseThrow(() -> { //example of supplier btw
            log.info("Task with such [id: {}] doesn't exists", taskId);
            return new EntityNotFoundException("Task with such [id: %s] doesn't exists".formatted(taskId));
        }); //if value!=null -> return value, else - throw supplier.get()-> supplier is lambda
        return taskMapper.toModel(taskEntity);
    }

    @Override
    public List<Task> getPerformedTasks(String username) {
        return taskMapper.toModels(taskRepository.findAllByUsername(username));
    }

    @Override
    public List<Task> getCurrentTasks(String username) {
        return taskMapper.toModels(taskRepository.findAllByUsernameCurrent(username));
    }

}
