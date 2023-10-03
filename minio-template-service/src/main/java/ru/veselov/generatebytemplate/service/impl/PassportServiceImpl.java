package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.SerialNumberDto;
import ru.veselov.generatebytemplate.service.PassportGeneratorService;
import ru.veselov.generatebytemplate.service.PassportService;
import ru.veselov.generatebytemplate.service.PassportTemplateService;
import ru.veselov.generatebytemplate.service.PdfService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    @Value("${spring.kafka.passport-topic}")
    private String topic;

    private final PassportGeneratorService passportGeneratorService;

    private final PdfService pdfService;

    private final PassportTemplateService passportTemplateService;

    private final KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate;

    @Override
    public byte[] createPassportsPdf(GeneratePassportsDto generatePassportsDto) {
        log.info("Starting process of generating passports");
        ByteArrayResource templateByteArrayResource = passportTemplateService
                .getTemplate(generatePassportsDto.getTemplateId());
        List<String> serials = generatePassportsDto.getSerials()
                .stream().map(SerialNumberDto::getSerial).toList();
        byte[] sourceBytes = passportGeneratorService
                .generatePassports(
                        serials,
                        templateByteArrayResource,
                        getFormattedDate(generatePassportsDto.getPrintDate()));
        byte[] pdfBytes = pdfService.createPdf(sourceBytes);
        log.debug("Sending message to broker: [{}]", generatePassportsDto);
        sendToMessageBroker(generatePassportsDto);
        return pdfBytes;
    }

    private void sendToMessageBroker(GeneratePassportsDto generatePassportsDto) {
        Message<GeneratePassportsDto> message = MessageBuilder.withPayload(generatePassportsDto)
                .setHeader(KafkaHeaders.TOPIC, topic).build();
        CompletableFuture<SendResult<String, GeneratePassportsDto>> send =
                kafkaTemplate.send(message);
        send.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message successfully sent to Kafka broker, to [topic {}: message: {}]",
                        topic, generatePassportsDto);
            } else {
                log.error("Message wan not sent to broker with exception: {}", ex.getMessage());
            }
        });
    }

    private String getFormattedDate(LocalDate localDate) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
        return dateTimeFormatter.format(localDate);
    }

}
