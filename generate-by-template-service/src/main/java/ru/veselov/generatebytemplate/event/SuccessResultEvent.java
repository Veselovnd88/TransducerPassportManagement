package ru.veselov.generatebytemplate.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class SuccessResultEvent extends ResultEvent {

    @JsonIgnore
    public static final String URL = "/api/v1/generate/result/";

    private String resultFileId;

    private String endpoint;

    public SuccessResultEvent(String resultFileId, String message) {
        super(message, EventType.READY);
        this.resultFileId = resultFileId;
        this.endpoint = URL + this.resultFileId;
    }

}
