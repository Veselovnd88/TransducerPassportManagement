package ru.veselov.miniotemplateservice.exception;

import lombok.Getter;

public class PageExceedsMaximumValueException extends RuntimeException {

    @Getter
    private final int currentPage;

    public PageExceedsMaximumValueException(String message, int page) {
        super(message);
        this.currentPage = page;
    }

}
