package ru.veselov.taskservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.FluxSink;

import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = "fluxSink")
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionData {

    private UUID subId;

    private String taskId;

    private FluxSink<ServerSentEvent<String>> fluxSink;

}
