package ru.veselov.generatebytemplate.dto;

import ru.veselov.generatebytemplate.event.TaskStatus;

public record TaskResultDto(String fileId, String errorMessage, TaskStatus taskStatus) {
}
