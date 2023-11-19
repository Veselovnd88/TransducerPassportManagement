package ru.veselov.generatebytemplate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResultEvent extends ResultEvent {

    private String errorMessage;

    public ErrorResultEvent(UUID taskId, String errorMessage) {
        super(taskId, EventType.ERROR);
        this.errorMessage = errorMessage;
    }

}
