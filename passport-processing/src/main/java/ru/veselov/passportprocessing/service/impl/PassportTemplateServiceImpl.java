package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.exception.DocxProcessingException;
import ru.veselov.passportprocessing.service.PassportTemplateService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

    @Override
    public InputStream getTemplate(String templateId) {
        String path =
                //"C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
                "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file.docx";
        //  "C:\\Users\\Nikolay\\IdeaProjects\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\file.docx";
        Path file = Path.of(path);
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(file);
        } catch (IOException e) {
            log.error("Error occurred during opening input streams from .docx file");
            throw new DocxProcessingException(e.getMessage(), e);
        }
        return inputStream;
    }
}
