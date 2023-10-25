package ru.veselov.generatebytemplate.exception;

public class DocxProcessingException extends RuntimeException {

    public DocxProcessingException(String message) {
        super(message);
    }

    public DocxProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
