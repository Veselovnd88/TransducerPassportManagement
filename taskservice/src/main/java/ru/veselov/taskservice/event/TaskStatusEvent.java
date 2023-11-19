package ru.veselov.taskservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusEvent {

    private String taskId;

    private EventType eventType;

}
