package ru.veselov.taskservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.service.TaskStatusEventService;

@RestController
@RequestMapping("/api/v1/task/status-stream")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TaskStatusSseController {

    private final TaskStatusEventService taskStatusEventService;

    @GetMapping(value = "/{taskId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StatusStreamMessage>> getStatusStream(@PathVariable("taskId") @UUID String taskId) {
        return taskStatusEventService.createSubscription(taskId);
    }

}
