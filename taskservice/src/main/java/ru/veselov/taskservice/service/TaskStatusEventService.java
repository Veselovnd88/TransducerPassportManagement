package ru.veselov.taskservice.service;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import ru.veselov.taskservice.events.StatusStreamMessage;

public interface TaskStatusEventService {

    Flux<ServerSentEvent<StatusStreamMessage>> createSubscription(String taskId);

}
