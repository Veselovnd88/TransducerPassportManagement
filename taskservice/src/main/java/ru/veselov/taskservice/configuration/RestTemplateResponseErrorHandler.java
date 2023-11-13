package ru.veselov.taskservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.veselov.taskservice.exception.ErrorHandlingException;
import ru.veselov.taskservice.exception.GenerateServiceException;
import ru.veselov.taskservice.exception.GenerateServiceValidationException;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    private static final String ERROR_MSG_LOG = "Validation error from GenerateService: status {},  response {}";

    private static final String ERROR_MSG_EXCEPTION = "Validation error from GenerateService: status %s,  response %s";

    public static final String GENERATE_SERVICE_ERROR = "Generate service error occurred";

    private final ObjectMapper jacksonObjectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) {
        try {
            return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
        } catch (IOException e) {
            throw new ErrorHandlingException(e.getMessage(), e);
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) {
        try {
            if (response.getStatusCode().is4xxClientError()) {
                String errorMessage = writeErrorResponse(response);
                ValidationErrorResponse validationErrorResponse = jacksonObjectMapper
                        .readValue(errorMessage, ValidationErrorResponse.class);
                log.error(ERROR_MSG_LOG, response.getStatusCode(), validationErrorResponse);
                throw new GenerateServiceValidationException(ERROR_MSG_EXCEPTION
                        .formatted(response.getStatusCode(), validationErrorResponse));
            }

            if (response.getStatusCode().is5xxServerError()) {
                log.error(GENERATE_SERVICE_ERROR);
                throw new GenerateServiceException(GENERATE_SERVICE_ERROR);
            }
        } catch (IOException e) {
            throw new ErrorHandlingException(e.getMessage(), e);
        }
    }

    private String writeErrorResponse(ClientHttpResponse response) throws IOException {
        InputStream body = response.getBody();
        StringWriter writer = new StringWriter();
        IOUtils.write(body.readAllBytes(), writer, Encoding.DEFAULT_CHARSET);
        return writer.toString();
    }

}
