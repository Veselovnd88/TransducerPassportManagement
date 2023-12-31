package ru.veselov.generatebytemplate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.exception.ServiceUnavailableException;
import ru.veselov.generatebytemplate.service.PdfHttpClient;

@Service
@Slf4j
public class PdfHttpClientImpl implements PdfHttpClient {

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    @Value("${pdf-service.url}")
    private String pdfConverterUrl;

    @Value("${pdf-service.filename}")
    private String filename;

    private final WebClient webClient;

    @Autowired
    public PdfHttpClientImpl(@Qualifier("simpleWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public DataBuffer sendRequestForConvertingDocxToPdf(ByteArrayResource source) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", source.getByteArray())
                .header(CONTENT_DISPOSITION, "form-data; name=" + filename).filename(filename + ".docx");
        Mono<DataBuffer> dataBufferMono = webClient.post().uri(pdfConverterUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error(
                            "Can't convert to Pdf, something wrong with generated docx file, [status: {}, message: {}]",
                            response.statusCode().value(), "Bad request");
                    throw new PdfProcessingException(
                            "Can't convert to Pdf, something wrong with generated docx file, status: %s, message: %s"
                                    .formatted(response.statusCode(), "Bad request"));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Something wrong with Pdf service, [status: {}, message: {}]",
                            response.statusCode().value(), "Pdf service unavailable");
                    throw new ServiceUnavailableException(
                            "Something wrong with Pdf service, [status: %s, message: %s]".formatted(
                                    response.statusCode().value(), "Pdf service unavailable")
                    );
                })
                .bodyToMono(DataBuffer.class).doOnError(t -> log.error("Pdf server is down: [{}]", t.getMessage()))
                .onErrorResume(WebClientRequestException.class::isInstance, e -> {
                    throw new ServiceUnavailableException("Pdf server is down: %s".formatted(e.getMessage()));
                });
        return dataBufferMono.block();
    }

}
