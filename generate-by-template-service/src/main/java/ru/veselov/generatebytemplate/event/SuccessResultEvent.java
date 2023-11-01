package ru.veselov.generatebytemplate.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SuccessResultEvent extends ResultEvent {

    private String resultFileId;

    public SuccessResultEvent(UUID taskId, String resultFileId, String message) {
        super(taskId, message, EventType.READY);
        this.resultFileId = resultFileId;
    }

}
