package ru.veselov.generatebytemplate.exception;

public class TaskExecutionException extends RuntimeException {

    public TaskExecutionException(String message, Exception exception) {
        super(message, exception);
    }
}
