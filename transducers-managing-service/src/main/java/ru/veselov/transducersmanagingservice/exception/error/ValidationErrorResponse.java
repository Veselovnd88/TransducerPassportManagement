package ru.veselov.transducersmanagingservice.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ApiErrorResponse {

    private ErrorCode errorCode = ErrorCode.ERROR_VALIDATION;

    private List<ViolationError> violations;

    public ValidationErrorResponse(String message, List<ViolationError> violations) {
        super(ErrorCode.ERROR_VALIDATION, message);
        this.violations = violations;
    }

}
