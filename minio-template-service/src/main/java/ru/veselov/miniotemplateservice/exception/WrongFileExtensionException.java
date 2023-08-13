package ru.veselov.miniotemplateservice.exception;

public class WrongFileExtensionException extends RuntimeException {
    public WrongFileExtensionException(String message) {
        super(message);
    }
}
