package ru.veselov.passportprocessing.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ru.veselov.passportprocessing.exception.ServiceUnavailableException;
import ru.veselov.passportprocessing.exception.TemplateNotExistsException;
import ru.veselov.passportprocessing.exception.TemplateStorageException;
import ru.veselov.passportprocessing.service.TemplateStorageHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class TemplateStorageHttpClientImpl implements TemplateStorageHttpClient {

    @Value("${template-storage.url}")
    private String templateStorageUrl;
    @Qualifier("lbWebClient")
    private final WebClient webClient;

    public TemplateStorageHttpClientImpl(@Qualifier("lbWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public ByteArrayResource sendRequestToGetTemplate(String templateId) {
        String fullUrl = templateStorageUrl + "/source/id/" + templateId;
        Mono<DataBuffer> dataBufferMono = webClient.get().uri(fullUrl)
                .retrieve().onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    throw new TemplateNotExistsException("Template with [id: %s] doesnt exists".formatted(templateId));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    throw new ServiceUnavailableException("Error occurred during retrieving template with [id: %s]"
                            .formatted(templateId));
                }).bodyToMono(DataBuffer.class)
                .doOnError(t -> log.error("Template Storage server is down: {}", t.getMessage()))
                .onErrorResume(WebClientRequestException.class::isInstance, e -> {
                    throw new ServiceUnavailableException("Template Storage server is down: %s"
                            .formatted(e.getMessage()));
                });
        DataBuffer dataBuffer = dataBufferMono.block();
        return bufferToByteArrayResource(dataBuffer);
    }

    private ByteArrayResource bufferToByteArrayResource(DataBuffer dataBuffer) {
        if (dataBuffer == null) {
            String errorMessage = "Template storage service doesn't return correct byte array";
            log.error(errorMessage);
            throw new TemplateStorageException(errorMessage);
        }
        try (InputStream inputStream = dataBuffer.asInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            inputStream.transferTo(outputStream);
            log.info("Template converted to byte array resource");
            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            String errorMessage = "Can't create byte array from template input stream";
            log.error(errorMessage + ": " + e.getMessage());
            throw new TemplateStorageException(errorMessage + ": " + e.getMessage());
        }
    }

}
