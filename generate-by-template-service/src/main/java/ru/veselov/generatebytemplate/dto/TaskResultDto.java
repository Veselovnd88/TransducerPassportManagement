package ru.veselov.generatebytemplate.dto;

import ru.veselov.generatebytemplate.event.EventType;

public record TaskResultDto(String fileId, String message, String errorMessage, EventType eventType) {
}
