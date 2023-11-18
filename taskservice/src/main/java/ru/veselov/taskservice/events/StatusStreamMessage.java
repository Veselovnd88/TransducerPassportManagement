package ru.veselov.taskservice.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.taskservice.model.Task;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusStreamMessage {

    private Task task;

    private String taskId;

    private String message;

}
