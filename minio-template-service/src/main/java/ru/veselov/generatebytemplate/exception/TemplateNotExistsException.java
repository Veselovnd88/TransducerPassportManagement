package ru.veselov.generatebytemplate.exception;

public class TemplateNotExistsException extends RuntimeException {

    public TemplateNotExistsException(String message) {
        super(message);
    }
}
