package ru.veselov.generatebytemplate.exception;

public class TemplateNotFoundException extends RuntimeException {

    public TemplateNotFoundException(String message) {
        super(message);
    }
}
