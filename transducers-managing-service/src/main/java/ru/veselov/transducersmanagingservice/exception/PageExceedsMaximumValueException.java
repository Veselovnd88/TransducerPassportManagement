package ru.veselov.transducersmanagingservice.exception;

public class PageExceedsMaximumValueException extends RuntimeException {
    public PageExceedsMaximumValueException(String message, int page) {
    }
}
