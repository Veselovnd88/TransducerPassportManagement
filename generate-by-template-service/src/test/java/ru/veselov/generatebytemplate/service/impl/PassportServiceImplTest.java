package ru.veselov.generatebytemplate.service.impl;

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
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.SerialNumberDto;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.DocxPassportService;
import ru.veselov.generatebytemplate.service.GeneratedResultFileService;
import ru.veselov.generatebytemplate.service.PdfService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class PassportServiceImplTest {

    public static List<SerialNumberDto> SERIALS_DTOS = List.of(
            new SerialNumberDto("1", UUID.randomUUID().toString()),
            new SerialNumberDto("2", UUID.randomUUID().toString()),
            new SerialNumberDto("3", UUID.randomUUID().toString()));

    public static List<String> SERIALS = List.of("1", "2", "3");

    public static ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final LocalDate DATE = LocalDate.now();

    @Mock
    DocxPassportService docxPassportService;

    @Mock
    PdfService pdfService;

    @Mock
    GeneratedResultFileService generatedResultFileService;

    @Mock
    KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate;

    @InjectMocks
    PassportServiceImpl passportService;

    @Captor
    ArgumentCaptor<Message<GeneratePassportsDto>> messageArgumentCaptor;

    @Captor
    ArgumentCaptor<GeneratedResultFile> generatedResultFileArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(passportService, "dateFormat", DATE_FORMAT, String.class);
    }

    @Test
    void shouldCallServicesForReturningByteArray() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        /*Mockito.when(passportTemplateService.getTemplate(ArgumentMatchers.anyString())).thenReturn(byteArrayResource);
        Mockito.when(docxGeneratorService.generateDocx(SERIALS, byteArrayResource, DTF.format(DATE)))
                .thenReturn(TestUtils.SOURCE_BYTES);*/
        Mockito.when(pdfService.createPdf(ArgumentMatchers.any())).thenReturn(byteArrayResource);
        CompletableFuture mockCF = Mockito.mock(CompletableFuture.class);
        Mockito.when(kafkaTemplate.send(ArgumentMatchers.any(Message.class))).thenReturn(mockCF);

        passportService.createPassportsPdf(generatePassportsDto);
        Mockito.verify(docxPassportService, Mockito.times(1)).createDocxPassports(generatePassportsDto);
        /*Mockito.verify(passportTemplateService, Mockito.times(1)).getTemplate(generatePassportsDto.getTemplateId());
        Mockito.verify(docxGeneratorService, Mockito.times(1))
                .generateDocx(SERIALS, byteArrayResource, DTF.format(DATE));*/
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(byteArrayResource);
        Mockito.verify(generatedResultFileService, Mockito.times(1))
                .save(ArgumentMatchers.any(ByteArrayResource.class), generatedResultFileArgumentCaptor.capture());

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(messageArgumentCaptor.capture());
        Message<GeneratePassportsDto> captured = messageArgumentCaptor.getValue();
        Assertions.assertThat(captured.getPayload()).isEqualTo(generatePassportsDto);
    }

}
