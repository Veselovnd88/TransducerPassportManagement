package ru.veselov.taskservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskLaunchService;
import ru.veselov.taskservice.utils.AppConstants;

@RestController
@RequestMapping("/api/v1/task/launch")
@Validated
@RequiredArgsConstructor
public class TaskLaunchController {

    private final TaskLaunchService taskLaunchService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Task createTask(@RequestBody @Valid GeneratePassportsDto generatePassportsDto,
                           @RequestHeader(value = AppConstants.SERVICE_USERNAME_HEADER) @NotEmpty String username) {
        return taskLaunchService.launchTask(generatePassportsDto, username);
    }

}
