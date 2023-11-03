package ru.veselov.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/task")
@Validated
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Task createTask(@RequestBody @Valid GeneratePassportsDto generatePassportsDto,
                           @RequestHeader("username") String username) {
        return taskService.createTask(generatePassportsDto, username);
    }

    @GetMapping("/{taskId}")
    public Task getTask(@PathVariable("taskId") @UUID String taskId) {
        return taskService.getTask(taskId);
    }

    @GetMapping("/performed")
    public List<Task> getPerformedTasks(@RequestHeader(name = "username") String username) {
        return taskService.getPerformedTasks(username);
    }

    @GetMapping("/current")
    public List<Task> getCurrentTasks(@RequestHeader(name = "username") String username) {
        return taskService.getCurrentTasks(username);
    }

}
