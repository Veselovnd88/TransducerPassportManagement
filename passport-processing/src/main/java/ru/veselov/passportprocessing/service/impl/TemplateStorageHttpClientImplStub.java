package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import ru.veselov.passportprocessing.exception.DocxProcessingException;
import ru.veselov.passportprocessing.service.TemplateStorageHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "template", name = "stub", havingValue = "enabled")
public class TemplateStorageHttpClientImplStub implements TemplateStorageHttpClient {

    private final WebClient webClient;

    @Override
    public ByteArrayResource sendRequestToGetTemplate(String templateId) {
        String path =
        // "C:\\Users\\VeselovND\\git\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\file-temp.docx";
         //       "C:\\Users\\VeselovND\\git\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\803751 10.0 1 бар М20 2РМД.docx";
                // "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file-temp.docx";
        //  "C:\\Users\\Nikolay\\IdeaProjects\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\file-temp.docx";
            //    "C:\\Users\\VeselovND\\git\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\803753 10.0 100 бар М20 2РМД.docx"
               // "C:\\Users\\VeselovND\\git\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\803752 10.0 40 бар М20 2РМД.docx"
                "C:\\Users\\VeselovND\\git\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\805693 10.0 25 бар М20 2РМД.docx"
        ;
        Path file = Path.of(path);
        try {
            InputStream inputStream = Files.newInputStream(file);
            ByteArrayResource templateByteArrayResource = new ByteArrayResource(inputStream.readAllBytes());
            inputStream.close();
            log.info("Retrieved [template: {}] from storage", templateId);
            return templateByteArrayResource;
        } catch (IOException e) {
            log.error("Error occurred during opening input streams from .docx file");
            throw new DocxProcessingException(e.getMessage(), e);
        }
    }
}
