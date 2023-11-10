package ru.veselov.taskservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    private UUID taskId;

    private Boolean isPerformed;

    private LocalDateTime createdAt;

    private LocalDateTime performedAt;

}
