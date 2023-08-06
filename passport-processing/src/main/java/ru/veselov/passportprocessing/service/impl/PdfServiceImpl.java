package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
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
                .retrieve().bodyToMono(DataBuffer.class);
        //200 - ok, 400 - bad request, 503 - service unavailable
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
