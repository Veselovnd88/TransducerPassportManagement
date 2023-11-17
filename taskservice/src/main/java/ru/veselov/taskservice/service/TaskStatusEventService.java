package ru.veselov.taskservice.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.model.Task;

public interface TaskStatusEventService {

    Flux<ServerSentEvent<Task>> createSubscription(String taskId);

}
