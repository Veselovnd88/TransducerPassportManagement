package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.exception.DocxProcessingException;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PassportService;
import ru.veselov.passportprocessing.service.PdfService;

import java.io.ByteArrayInputStream;
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

    private final PassportGeneratorService passportGeneratorService;

    private final PdfService pdfService;

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
            throw new DocxProcessingException(e.getMessage(), e);
        }
        byte[] sourceBytes = passportGeneratorService
                .generatePassports(serials, inputStream, LocalDate.now().toString());
        byte[] pdf = pdfService.createPdf(sourceBytes);
        creatFile(pdf);
    }

    private void creatFile(byte[] pdfBytes) {
        OutputStream os = null;
        try {
            os = new FileOutputStream("sample.pdf");
            ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
            IOUtils.copy(bais, os);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
