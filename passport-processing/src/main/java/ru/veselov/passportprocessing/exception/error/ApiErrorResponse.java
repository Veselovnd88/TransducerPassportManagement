package ru.veselov.passportprocessing.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiErrorResponse {

    private ErrorCode errorCode;

    private String message;

}
