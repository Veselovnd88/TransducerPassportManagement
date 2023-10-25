package ru.veselov.generatebytemplate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class ResultEvent {

    private String message;

    private EventType eventType;

}
