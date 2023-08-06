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

    private List<String> testSerials = new ArrayList<>();

    @Override
    public byte[] createPassportsPdf(List<String> serials, String templateId, String date) {
        for (int i = 1; i < 20; i++) {
            testSerials.add(i + "+number+" + i);
        }
        serials=testSerials;
        String path =
             //   "C:\\Users\\VeselovND\\git\\PTPassportProject\\document-processing\\document-processing\\src\\main\\resources\\file.docx";
        //  "/home/nikolay/git/PTPassportProject/document-processing/document-processing/src/main/resources/file.docx";
        "C:\\Users\\Nikolay\\IdeaProjects\\TransducerPassportManagement\\passport-processing\\src\\main\\resources\\file.docx";
        Path file = Path.of(path);
        InputStream inputStream;
        try {
            inputStream = Files.newInputStream(file);
        } catch (IOException e) {
            log.error("Error occurred during opening input streams from .docx file");
            throw new DocxProcessingException(e.getMessage(), e);
        }
        byte[] sourceBytes = passportGeneratorService
                .generatePassports(serials, inputStream, LocalDate.now().toString());
        byte[] pdfBytes = pdfService.createPdf(sourceBytes);
        creatFile(pdfBytes);
        return pdfBytes;
    }

    private void creatFile(byte[] pdfBytes) {
        OutputStream os;
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
