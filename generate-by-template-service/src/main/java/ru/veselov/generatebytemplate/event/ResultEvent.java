package ru.veselov.generatebytemplate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class ResultEvent {

    private UUID taskId;

    private String message;

    private EventType eventType;

}
