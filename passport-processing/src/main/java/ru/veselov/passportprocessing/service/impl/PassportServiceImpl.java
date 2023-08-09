package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PassportService;
import ru.veselov.passportprocessing.service.PassportTemplateService;
import ru.veselov.passportprocessing.service.PdfService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Slf4j
public class PassportServiceImpl implements PassportService {

    @Value("${placeholder.date-format}")
    private String dateFormat;

    private final PassportGeneratorService passportGeneratorService;

    private final PdfService pdfService;

    private final PassportTemplateService passportTemplateService;

    @Override
    public byte[] createPassportsPdf(GeneratePassportsDto generatePassportsDto) {
        log.info("Starting process of generating passports");
        ByteArrayResource templateByteArrayResource = passportTemplateService
                .getTemplate(generatePassportsDto.getTemplateId());
        byte[] sourceBytes = passportGeneratorService
                .generatePassports(
                        generatePassportsDto.getSerials(),
                        templateByteArrayResource,
                        getFormattedDate(generatePassportsDto.getDate()));
        return pdfService.createPdf(sourceBytes);
    }

    private String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTimeFormatter.format(localDate);
    }

}
