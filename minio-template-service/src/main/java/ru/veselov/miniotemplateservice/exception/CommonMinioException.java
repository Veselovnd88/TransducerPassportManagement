package ru.veselov.miniotemplateservice.exception;

public class CommonMinioException extends RuntimeException {

    public CommonMinioException(String message, Throwable cause) {
        super(message, cause);
    }
}
