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
import ru.veselov.generatebytemplate.event.ResultEventPublisher;
import ru.veselov.generatebytemplate.exception.DocxProcessingException;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.exception.ServiceUnavailableException;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.DocxPassportService;
import ru.veselov.generatebytemplate.service.GeneratedResultFileService;
import ru.veselov.generatebytemplate.service.PdfService;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"rawtypes", "unchecked"})
class PassportServiceImplTest {

    public static ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    @Mock
    DocxPassportService docxPassportService;

    @Mock
    PdfService pdfService;

    @Mock
    GeneratedResultFileService generatedResultFileService;

    @Mock
    ResultEventPublisher eventPublisher;

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
    void shouldGenerateAndCreatePdfPassportsAndPublishEvent() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        GeneratedResultFile returnedResult = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.when(pdfService.createPdf(ArgumentMatchers.any())).thenReturn(byteArrayResource);
        Mockito.when(generatedResultFileService.save(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(returnedResult);
        CompletableFuture mockCF = Mockito.mock(CompletableFuture.class);
        Mockito.when(kafkaTemplate.send(ArgumentMatchers.any(Message.class))).thenReturn(mockCF);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(docxPassportService, Mockito.times(1)).createDocxPassports(generatePassportsDto);
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(byteArrayResource);
        Mockito.verify(generatedResultFileService, Mockito.times(1))
                .save(ArgumentMatchers.any(ByteArrayResource.class), generatedResultFileArgumentCaptor.capture());

        Mockito.verify(eventPublisher, Mockito.times(1)).publishSuccessResultEvent(returnedResult);

        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(messageArgumentCaptor.capture());
        Message<GeneratePassportsDto> captured = messageArgumentCaptor.getValue();
        Assertions.assertThat(captured.getPayload()).isEqualTo(generatePassportsDto);
    }

    @Test
    void shouldPublishErrorEventWhenDocxExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.doThrow(DocxProcessingException.class)
                .when(docxPassportService).createDocxPassports(generatePassportsDto);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(ArgumentMatchers.any());
        Mockito.verify(pdfService, Mockito.never()).createPdf(ArgumentMatchers.any());
        Mockito.verify(generatedResultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.any(Message.class));
    }

    @Test
    void shouldPublishErrorEventWhenPdfExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();

        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(PdfProcessingException.class)
                .when(pdfService).createPdf(byteArrayResource);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(ArgumentMatchers.any());
        Mockito.verify(generatedResultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.any(Message.class));
    }

    @Test
    void shouldPublishErrorEventWhenServiceUnavailableExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();

        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(ServiceUnavailableException.class)
                .when(pdfService).createPdf(byteArrayResource);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(ArgumentMatchers.any());
        Mockito.verify(generatedResultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaTemplate, Mockito.never()).send(ArgumentMatchers.any(Message.class));
    }


}