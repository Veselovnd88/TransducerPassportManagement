package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.event.ResultEventPublisher;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.exception.DocxProcessingException;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.exception.ServiceUnavailableException;
import ru.veselov.generatebytemplate.exception.TemplateNotFoundException;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.service.DocxPassportService;
import ru.veselov.generatebytemplate.service.KafkaBrokerSender;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.PdfService;
import ru.veselov.generatebytemplate.service.ResultFileService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Service responsible for all actions for generating passports and pdf:
 * -get template from storage microservice;
 * -generate .docx with all serial numbers;
 * -send to pdf service for converting to pdf;
 * -send Dto to broker after getting result, and return pdf bytes to user
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PassportServiceImpl implements PassportService {

    @Value("${placeholder.date-format}")
    private String dateFormat;

    private final DocxPassportService docxPassportService;

    private final PdfService pdfService;

    private final KafkaBrokerSender kafkaBrokerSender;

    private final ResultFileService resultFileService;

    private final ResultEventPublisher resultEventPublisher;

    @Async(value = "asyncThreadPoolTaskExecutor")
    @Override
    public void createPassportsPdf(GeneratePassportsDto generatePassportsDto, String taskId, String username) {
        log.info("Starting process of generating passports");
        try {
            ByteArrayResource docxPassports = docxPassportService.createDocxPassports(generatePassportsDto);
            ByteArrayResource pdfBytes = pdfService.createPdf(docxPassports);
            ResultFile rawResultFile = createResultFile(generatePassportsDto, taskId, username);
            log.debug("Saving generated file to storage");
            ResultFile savedResult = resultFileService.save(pdfBytes, rawResultFile);
            resultEventPublisher.publishSuccessResultEvent(savedResult);
            log.debug("Sending message to broker: [{}]", generatePassportsDto);
            kafkaBrokerSender.sendPassportInfoMessage(generatePassportsDto);
        } catch (CommonMinioException |
                 PdfProcessingException |
                 DocxProcessingException |
                 TemplateNotFoundException | ServiceUnavailableException e) {
            log.error("Error occurred during generating docx and pdf file with passports");
            resultEventPublisher.publishErrorResultEvent(taskId, e);
        }
    }

    private String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTimeFormatter.format(localDate);
    }

    private ResultFile createResultFile(GeneratePassportsDto generatePassportsDto, String taskId, String username) {
        return ResultFile.builder()
                .filename(createFilenameFromGeneratePassportsDto(generatePassportsDto))
                .templateId(generatePassportsDto.getTemplateId())
                .taskId(taskId)
                .username(username)
                .build();
    }

    private String createFilenameFromGeneratePassportsDto(GeneratePassportsDto generatePassportsDto) {
        String templateId = generatePassportsDto.getTemplateId();
        LocalDate printDate = generatePassportsDto.getPrintDate();
        int serialsCount = generatePassportsDto.getSerials().size();
        String formattedPrintDate = getFormattedDate(printDate);
        long dateTimePostfix = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return templateId + "-" + formattedPrintDate + "-" + serialsCount + dateTimePostfix;
    }

}
