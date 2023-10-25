package ru.veselov.generatebytemplate.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SuccessResultEvent extends ResultEvent {

    private String resultFileId;

    private String endpoint;

    public SuccessResultEvent(String resultFileId, String message) {
        super(message, EventType.READY);
        this.resultFileId = resultFileId;
    }

}
