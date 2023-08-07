package ru.veselov.passportprocessing.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.veselov.passportprocessing.exception.error.ApiErrorResponse;
import ru.veselov.passportprocessing.exception.error.ErrorCode;

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

}
