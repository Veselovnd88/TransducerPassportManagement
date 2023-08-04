package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.veselov.passportprocessing.exception.DocxProcessingException;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PassportService;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class PassportServiceImpl implements PassportService {

    private final WebClient webClient = WebClient.create();

    private final PassportGeneratorService passportGeneratorService;

    private List<String> serials = new ArrayList<>();

    public void createPassportsPdf() {
        for (int i = 1; i < 20; i++) {
            serials.add(i + "+number+" + i);
        }
        String path =
                "C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
        //  "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file.docx";
        Path file = Path.of(path);
        InputStream inputStream = null;
        try {
            inputStream = Files.newInputStream(file);
        } catch (IOException e) {
            log.error("Error occurred during opening inputstreams from .docx file");
            throw new DocxProcessingException(e.getMessage());
        }
        byte[] bytes = passportGeneratorService.generatePassports(serials, inputStream, LocalDate.now().toString());
        createPdf(bytes);


    }


    private void createPdf(byte[] bytes) {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", bytes).header("Content-Disposition",
                "form-data; name=file").filename("file.docx");
        Mono<DataBuffer> dataBufferMono = webClient.post().uri("http://localhost:3000/forms/libreoffice/convert")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                .retrieve().bodyToMono(DataBuffer.class);

        //convert receivedByteArrayToPdfFile
        DataBuffer block = dataBufferMono.block();
        InputStream inputStream = block.asInputStream();
        OutputStream os = null;
        try {
            os = new FileOutputStream("sample.pdf");
            IOUtils.copy(inputStream, os);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
