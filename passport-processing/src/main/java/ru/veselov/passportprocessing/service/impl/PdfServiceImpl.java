package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private final WebClient webClient = WebClient.create();

    @Override
    public byte[] createPdf(byte[] source) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", source).header("Content-Disposition",
                "form-data; name=file").filename("file.docx");
        Mono<DataBuffer> dataBufferMono = webClient.post().uri("http://localhost:3000/forms/libreoffice/convert")
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
        if (pdfDatabuffer == null) {
            throw new RuntimeException();
        }
        InputStream pdfInputStream = pdfDatabuffer.asInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            pdfInputStream.transferTo(baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return baos.toByteArray();
    }

}
