package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;
import ru.veselov.passportprocessing.exception.PdfProcessingException;
import ru.veselov.passportprocessing.exception.ServiceUnavailableException;
import ru.veselov.passportprocessing.service.PdfService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    @Value("${pdf-service.url}")
    private String pdfConverterUrl;

    @Value("${pdf-service.filename}")
    private String filename;

    private final WebClient webClient;

    @Override
    public byte[] createPdf(byte[] source) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", source)
                .header(CONTENT_DISPOSITION, "form-data; name=" + filename).filename(filename + ".docx");
        Mono<DataBuffer> dataBufferMono = webClient.post().uri(pdfConverterUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    log.error(
                            "Can't convert to Pdf, something wrong with generated docx file, [status: {}, message: {}]",
                            response.statusCode().value(), response.bodyToMono(String.class).block());
                    throw new PdfProcessingException(
                            "Can't convert to Pdf, something wrong with generated docx file, status: %s, message: %s"
                                    .formatted(response.statusCode().value(),
                                            response.bodyToMono(String.class).block()));
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("Something wrong with Pdf service, [status: {}, message: {}]",
                            response.statusCode().value(), response.bodyToMono(String.class).block());
                    throw new ServiceUnavailableException(
                            "Something wrong with Pdf service, [status: %s, message: %s]".formatted(
                                    response.statusCode().value(), response.bodyToMono(String.class).block()
                            )
                    );
                })
                .bodyToMono(DataBuffer.class).doOnError(t -> log.error("Pdf server is down: [{}]", t.getMessage()))
                .onErrorResume(WebClientRequestException.class::isInstance, e -> {
                    throw new ServiceUnavailableException("Pdf server is down: %s".formatted(e.getMessage()));
                });
        DataBuffer pdfDatabuffer = dataBufferMono.block();
        log.info("Document successfully converted to pdf");
        return convertToByteArray(pdfDatabuffer);
    }

    private byte[] convertToByteArray(DataBuffer pdfDatabuffer) {
        if (pdfDatabuffer == null) {
            String errorMessage = "Pdf Service doesn't return correct byte array";
            log.error(errorMessage);
            throw new PdfProcessingException(errorMessage);
        }
        try (InputStream pdfInputStream = pdfDatabuffer.asInputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            pdfInputStream.transferTo(pdfOutputStream);
            log.info("Pdf converted to byte array");
            return pdfOutputStream.toByteArray();
        } catch (IOException e) {
            String errorMessage = "Can't create byte array from pdf input stream";
            log.error(errorMessage + ": " + e.getMessage());
            throw new PdfProcessingException(errorMessage + ": " + e.getMessage());
        }
    }

}
