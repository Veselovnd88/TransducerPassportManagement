package ru.veselov.taskservice.exception;

public class TaskNotStartedException extends RuntimeException {
    public TaskNotStartedException(String message) {
        super(message);
    }
}
