package ru.veselov.taskservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.veselov.taskservice.model.Task;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusEvent {

    private Task task;

    private EventType eventType;

}
