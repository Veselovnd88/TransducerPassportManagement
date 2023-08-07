package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PassportService;
import ru.veselov.passportprocessing.service.PdfService;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Slf4j
public class PassportServiceImpl implements PassportService {

    @Value("${}")
    private String dateFormat;

    private final PassportGeneratorService passportGeneratorService;

    private final PdfService pdfService;

    private final PassportTemplateService passportTemplateService;

    @Override
    public byte[] createPassportsPdf(GeneratePassportsDto generatePassportsDto) {
        log.info("Starting process of generating passports");
        InputStream templateInputStream = passportTemplateService.getTemplate(generatePassportsDto.getTemplateId());
        byte[] sourceBytes = passportGeneratorService
                .generatePassports(
                        generatePassportsDto.getSerials(),
                        templateInputStream,
                        getFormattedDate(generatePassportsDto.getDate()));
        return pdfService.createPdf(sourceBytes);
    }

    private String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTimeFormatter.format(localDate);
    }

    /*private void creatFile(byte[] pdfBytes) {
        OutputStream os;
        try {
            os = new FileOutputStream("sample.pdf");
            ByteArrayInputStream bais = new ByteArrayInputStream(pdfBytes);
            IOUtils.copy(bais, os);
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/


}
