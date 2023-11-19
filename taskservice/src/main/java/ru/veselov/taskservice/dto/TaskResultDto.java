package ru.veselov.taskservice.dto;

import ru.veselov.taskservice.entity.TaskStatus;

public record TaskResultDto(String fileId, String errorMessage, TaskStatus taskStatus) {
}
