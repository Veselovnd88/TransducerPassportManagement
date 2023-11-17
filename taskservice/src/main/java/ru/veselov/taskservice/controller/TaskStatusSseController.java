package ru.veselov.taskservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskStatusEventService;

@RestController
@RequestMapping("/api/v1/task/status-stream")
@RequiredArgsConstructor
@Slf4j
public class TaskStatusSseController {

    private final TaskStatusEventService taskStatusEventService;

    @GetMapping(value = "/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Task>> getStatusStream(@PathVariable("taskId") String taskId) {
        return taskStatusEventService.createSubscription(taskId);
    }

}