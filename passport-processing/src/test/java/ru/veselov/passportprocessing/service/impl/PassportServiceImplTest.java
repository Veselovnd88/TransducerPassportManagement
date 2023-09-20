package ru.veselov.passportprocessing.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.dto.SerialNumberDto;
import ru.veselov.passportprocessing.service.PassportGeneratorService;
import ru.veselov.passportprocessing.service.PassportTemplateService;
import ru.veselov.passportprocessing.service.PdfService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class PassportServiceImplTest {

    public static byte[] SOURCE = new byte[]{1, 2, 3, 4};

    public static List<SerialNumberDto> SERIALS_DTOS = List.of(
            new SerialNumberDto("1", UUID.randomUUID().toString()),
            new SerialNumberDto("2", UUID.randomUUID().toString()),
            new SerialNumberDto("3", UUID.randomUUID().toString()));
    public static List<String> SERIALS = List.of("1", "2", "3");

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final LocalDate DATE = LocalDate.now();

    @Mock
    PassportGeneratorService passportGeneratorService;

    @Mock
    PdfService pdfService;

    @Mock
    PassportTemplateService passportTemplateService;

    @Mock
    KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate;

    @InjectMocks
    PassportServiceImpl passportService;

    @Captor
    ArgumentCaptor<Message<GeneratePassportsDto>> argumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(passportService, "dateFormat", DATE_FORMAT, String.class);
    }

    @Test
    void shouldCallServicesForReturningByteArray() {
        GeneratePassportsDto generatePassportsDto = new GeneratePassportsDto(
                SERIALS_DTOS, UUID.randomUUID().toString(), DATE);
        ByteArrayResource byteArrayResource = new ByteArrayResource(SOURCE);
        Mockito.when(passportTemplateService.getTemplate(ArgumentMatchers.anyString()))
                .thenReturn(byteArrayResource);
        Mockito.when(passportGeneratorService.generatePassports(
                SERIALS, byteArrayResource, DTF.format(DATE)
        )).thenReturn(SOURCE);
        Mockito.when(pdfService.createPdf(ArgumentMatchers.any())).thenReturn(SOURCE);
        CompletableFuture mockCF = Mockito.mock(CompletableFuture.class);
        Mockito.when(kafkaTemplate.send(ArgumentMatchers.any(Message.class))).thenReturn(mockCF);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(passportTemplateService, Mockito.times(1)).getTemplate(ArgumentMatchers.anyString());
        Mockito.verify(passportGeneratorService, Mockito.times(1))
                .generatePassports(SERIALS, byteArrayResource, DTF.format(DATE));
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(SOURCE);

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(argumentCaptor.capture());
        Message<GeneratePassportsDto> captured = argumentCaptor.getValue();
        Assertions.assertThat(captured.getPayload()).isEqualTo(generatePassportsDto);
    }

}
