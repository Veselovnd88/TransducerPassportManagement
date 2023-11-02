package ru.veselov.taskservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskServiceImpl implements TaskService {
    @Override
    public Task createTask(GeneratePassportsDto generatePassportsDto) {
        return null;
    }

    @Override
    public Task getTask(String taskId) {
        return null;
    }

    @Override
    public List<Task> getPerformedTasks(String username) {
        return null;
    }

    @Override
    public List<Task> getCurrentTasks(String username) {
        return null;
    }

}
