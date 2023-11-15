package ru.veselov.taskservice.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface TaskStatusEventService {

    Flux<ServerSentEvent<String>> createSubscription(String taskId);

}
