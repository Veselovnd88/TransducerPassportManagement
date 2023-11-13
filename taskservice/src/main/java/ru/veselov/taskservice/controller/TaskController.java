package ru.veselov.taskservice.controller;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
@Validated
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable("taskId") @UUID String taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping("/performed")
    public List<Task> getPerformedTasks(@RequestHeader(name = "username") @NotEmpty String username) {
        return taskService.getPerformedTasks(username);
    }

    @GetMapping("/current")
    public List<Task> getCurrentTasks(@RequestHeader(name = "username") @NotEmpty String username) {
        return taskService.getNotPerformedTasks(username);
    }

}
