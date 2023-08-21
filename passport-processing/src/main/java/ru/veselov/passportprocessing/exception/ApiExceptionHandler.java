package ru.veselov.passportprocessing.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.passportprocessing.exception.error.ApiErrorResponse;
import ru.veselov.passportprocessing.exception.error.ErrorCode;
import ru.veselov.passportprocessing.exception.error.ValidationErrorResponse;
import ru.veselov.passportprocessing.exception.error.ViolationError;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {


    @ExceptionHandler(DocxProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleDocProcessingException(DocxProcessingException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_DOC_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(PdfProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handlePdfProcessingException(PdfProcessingException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_PDF_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiErrorResponse handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(TemplateStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleTemplateStorageException(TemplateStorageException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_DOC_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(TemplateNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleTemplateNotExistsException(TemplateNotExistsException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, e.getMessage());
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
                        error.getRejectedValue() != null ? (String) error.getRejectedValue().toString() : "null"))
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
