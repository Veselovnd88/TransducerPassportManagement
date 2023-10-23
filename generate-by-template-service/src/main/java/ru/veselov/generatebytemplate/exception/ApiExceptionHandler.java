package ru.veselov.generatebytemplate.exception;

import jakarta.persistence.EntityExistsException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.generatebytemplate.exception.error.ApiErrorResponse;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.exception.error.ValidationErrorResponse;
import ru.veselov.generatebytemplate.exception.error.ViolationError;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler {

    @ExceptionHandler(CommonMinioException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse handleMinioException(CommonMinioException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_FILE_STORAGE, exception.getMessage());
    }

    @ExceptionHandler(PageExceedsMaximumValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse handleMaxPageException(PageExceedsMaximumValueException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_MAX_PAGE, exception.getMessage());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorResponse handleEntityExistsException(EntityExistsException exception) {
        return new ApiErrorResponse(ErrorCode.ERROR_TEMPLATE_EXISTS, exception.getMessage());
    }

    @ExceptionHandler(DocxProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//FIXME
    public ApiErrorResponse handleDocProcessingException(DocxProcessingException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_DOC_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(PdfProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//FIXME
    public ApiErrorResponse handlePdfProcessingException(PdfProcessingException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_PDF_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)//FIXME
    public ApiErrorResponse handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(TemplateStorageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)//FIXME
    public ApiErrorResponse handleTemplateStorageException(TemplateStorageException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_DOC_PROCESSING, e.getMessage());
    }

    @ExceptionHandler(TemplateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleTemplateNotFoundException(TemplateNotFoundException e) {
        return new ApiErrorResponse(ErrorCode.ERROR_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ResultFileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorResponse handleResultFileNotFoundException(ResultFileNotFoundException e) {
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
