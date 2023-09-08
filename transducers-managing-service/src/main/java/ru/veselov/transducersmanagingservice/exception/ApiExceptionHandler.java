package ru.veselov.transducersmanagingservice.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.transducersmanagingservice.exception.error.ApiErrorResponse;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.exception.error.ValidationErrorResponse;
import ru.veselov.transducersmanagingservice.exception.error.ViolationError;

import java.time.format.DateTimeParseException;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(DateTimeParseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleDateTimeParseException(DateTimeParseException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_WRONG_DATE, e.getMessage());
    }

    @ExceptionHandler(NotOfficeXmlFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleNotOfficeXmlFileException(NotOfficeXmlFileException e) {
        log.warn("Not Office file was sent: [{}]", e.getMessage());
        return new ApiErrorResponse(ErrorCode.ERROR_BAD_FILE, e.getMessage());
    }

    @ExceptionHandler(ParseXlsxFileException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleXlsxFileParseException(ParseXlsxFileException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_XLSX_PARSE_ERROR, e.getMessage());
    }

    @ExceptionHandler(PageExceedsMaximumValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMaxPageException(PageExceedsMaximumValueException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_MAX_PAGE, exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleEntityExistsException(EntityExistsException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        List<ViolationError> violations = e.getConstraintViolations().stream()
                .map(v -> new ViolationError(
                        fieldNameFromPath(v.getPropertyPath().toString()),
                        v.getMessage(),
                        v.getInvalidValue().toString()))
                .toList();
        log.error("Validation error occurred: [{}-{}]", e.getMessage(), violations);
        return new ValidationErrorResponse(e.getMessage(), violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<ViolationError> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new ViolationError(error.getField(), error.getDefaultMessage(),
                        error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null"))
                .toList();
        log.error("Validation error occurred: [{}-{}]", e.getMessage(), violations);
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
