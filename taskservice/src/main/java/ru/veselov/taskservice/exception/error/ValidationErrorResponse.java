package ru.veselov.taskservice.exception.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationErrorResponse extends ApiErrorResponse {

    private ErrorCode errorCode = ErrorCode.ERROR_VALIDATION;

    private List<ViolationError> violations;

    public ValidationErrorResponse(String message, List<ViolationError> violations) {
        super(ErrorCode.ERROR_VALIDATION, message);
        this.violations = violations;
    }

}
