package ru.veselov.generatebytemplate.exception;

public class MinioBucketAdjustingException extends RuntimeException {

    public MinioBucketAdjustingException(String message, Exception e) {
        super(message, e);
    }
}
