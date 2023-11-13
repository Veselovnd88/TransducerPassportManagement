package ru.veselov.taskservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Component
@RequiredArgsConstructor
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {


    private final ObjectMapper jacksonObjectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is4xxClientError()) {
            InputStream body = response.getBody();
            StringWriter writer = new StringWriter();
            IOUtils.write(body.readAllBytes(), writer, Encoding.DEFAULT_CHARSET);
            String errorMessage = writer.toString();
            jacksonObjectMapper.readValue(errorMessage, ValidationErrorResponse.class);
        }
    }

}
