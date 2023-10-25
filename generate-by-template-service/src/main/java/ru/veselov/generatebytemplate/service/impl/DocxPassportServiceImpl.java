package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.SerialNumberDto;
import ru.veselov.generatebytemplate.service.DocxGeneratorService;
import ru.veselov.generatebytemplate.service.DocxPassportService;
import ru.veselov.generatebytemplate.service.PassportTemplateService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocxPassportServiceImpl implements DocxPassportService {

    @Value("${placeholder.date-format}")
    private String dateFormat;

    private final DocxGeneratorService docxGeneratorService;

    private final PassportTemplateService passportTemplateService;

    @Override
    public ByteArrayResource createDocxPassports(GeneratePassportsDto generatePassportsDto) {
        log.info("Creating docx file with all serial numbers with [template: {}]",
                generatePassportsDto.getTemplateId());
        ByteArrayResource templateByteArrayResource = passportTemplateService
                .getTemplate(generatePassportsDto.getTemplateId());
        List<String> serials = generatePassportsDto.getSerials()
                .stream().map(SerialNumberDto::getSerial).toList();
        byte[] sourceBytes = docxGeneratorService
                .generateDocx(
                        serials,
                        templateByteArrayResource,
                        getFormattedDate(generatePassportsDto.getPrintDate()));
        ByteArrayResource docxBytesResource = new ByteArrayResource(sourceBytes);
        log.info("Docx with all serial numbers created and converted to byte array resource for [template: {}]",
                generatePassportsDto.getTemplateId());
        return docxBytesResource;
    }

    private String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTimeFormatter.format(localDate);
    }
}
