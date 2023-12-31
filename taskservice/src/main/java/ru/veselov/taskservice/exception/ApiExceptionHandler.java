package ru.veselov.taskservice.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.taskservice.exception.error.ApiErrorResponse;
import ru.veselov.taskservice.exception.error.ErrorCode;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;
import ru.veselov.taskservice.exception.error.ViolationError;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, exception.getMessage());
    }


    @ExceptionHandler(GenerateServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleGenerateServiceException(GenerateServiceException exception) {
        return new ApiErrorResponse(ErrorCode.SERVICE_ERROR, exception.getMessage());
    }

    @ExceptionHandler(ErrorHandlingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleErrorHandlingException(ErrorHandlingException exception) {
        return new ApiErrorResponse(ErrorCode.SERVICE_ERROR, exception.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException exception) {
        ViolationError violationError = new ViolationError(
                "Request Header: " + exception.getHeaderName(),
                exception.getMessage(),
                "null");
        return new ValidationErrorResponse(exception.getMessage(), List.of(violationError));
    }

    @ExceptionHandler(GenerateServiceValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleGenerateServiceValidationException(GenerateServiceValidationException exception) {
        return new ApiErrorResponse(ErrorCode.SERVICE_ERROR, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException exception) {
        List<ViolationError> violationErrors = exception.getConstraintViolations().stream()
                .map(v -> new ViolationError(
                        fieldNameFromPath(v.getPropertyPath().toString()),
                        v.getMessage(),
                        v.getInvalidValue().toString()))
                .toList();
        return new ValidationErrorResponse(exception.getMessage(), violationErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ViolationError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ViolationError(error.getField(), error.getDefaultMessage(),
                        error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null"))
                .toList();
        return new ValidationErrorResponse(e.getMessage(), violations);
    }

    private String fieldNameFromPath(String path) {
        String[] split = path.split("\\.");
        if (split.length > 1) {
            return split[split.length - 1];
        }
        return path;
    }

}
