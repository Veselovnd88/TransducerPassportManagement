package ru.veselov.taskservice.exception;

public class ErrorHandlingException extends RuntimeException {
    public ErrorHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
