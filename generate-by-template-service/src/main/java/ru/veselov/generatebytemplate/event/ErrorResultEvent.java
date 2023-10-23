package ru.veselov.generatebytemplate.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResultEvent extends ResultEvent {

    private String errorMessage;

    public ErrorResultEvent(String errorMessage, String message) {
        super(message, EventType.ERROR);
        this.errorMessage = errorMessage;
    }

}
