package ru.veselov.generatebytemplate.service.impl;

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
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
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
import ru.veselov.generatebytemplate.service.PdfService;
import ru.veselov.generatebytemplate.service.ResultFileService;

@ExtendWith(MockitoExtension.class)
class PassportServiceImplTest {

    public static ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    @Mock
    DocxPassportService docxPassportService;

    @Mock
    PdfService pdfService;

    @Mock
    ResultFileService resultFileService;

    @Mock
    ResultEventPublisher eventPublisher;

    @Mock
    KafkaBrokerSender kafkaBrokerSender;

    @InjectMocks
    PassportServiceImpl passportService;

    @Captor
    ArgumentCaptor<ResultFile> generatedResultFileArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(passportService, "dateFormat", DATE_FORMAT, String.class);
    }

    @Test
    void shouldGenerateAndCreatePdfPassportsAndPublishEvent() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        ResultFile returnedResult = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.when(pdfService.createPdf(ArgumentMatchers.any())).thenReturn(byteArrayResource);
        Mockito.when(resultFileService.save(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(returnedResult);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(docxPassportService, Mockito.times(1)).createDocxPassports(generatePassportsDto);
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(byteArrayResource);
        Mockito.verify(resultFileService, Mockito.times(1))
                .save(ArgumentMatchers.any(ByteArrayResource.class), generatedResultFileArgumentCaptor.capture());
        Mockito.verify(kafkaBrokerSender, Mockito.times(1)).sendPassportInfoMessage(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishSuccessResultEvent(returnedResult);
    }

    @Test
    void shouldPublishErrorEventWhenDocxExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.doThrow(DocxProcessingException.class)
                .when(docxPassportService).createDocxPassports(generatePassportsDto);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(pdfService, Mockito.never()).createPdf(ArgumentMatchers.any());
        Mockito.verify(resultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaBrokerSender, Mockito.never()).sendPassportInfoMessage(ArgumentMatchers.any());
    }

    @Test
    void shouldPublishErrorEventWhenPdfExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();

        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(PdfProcessingException.class)
                .when(pdfService).createPdf(byteArrayResource);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(resultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaBrokerSender, Mockito.never()).sendPassportInfoMessage(ArgumentMatchers.any());
    }

    @Test
    void shouldPublishErrorEventWhenServiceUnavailableExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();

        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(ServiceUnavailableException.class)
                .when(pdfService).createPdf(byteArrayResource);

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(resultFileService, Mockito.never()).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaBrokerSender, Mockito.never()).sendPassportInfoMessage(ArgumentMatchers.any());
    }

    @Test
    void shouldPublishErrorEventWhenTemplateNotFoundExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.when(pdfService.createPdf(byteArrayResource))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(TemplateNotFoundException.class).when(resultFileService)
                .save(ArgumentMatchers.any(), ArgumentMatchers.any());

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(docxPassportService, Mockito.times(1)).createDocxPassports(generatePassportsDto);
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(byteArrayResource);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(resultFileService, Mockito.times(1)).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaBrokerSender, Mockito.never()).sendPassportInfoMessage(ArgumentMatchers.any());
    }

    @Test
    void shouldPublishErrorEventWhenMinioExceptionHappened() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto))
                .thenReturn(byteArrayResource);
        Mockito.when(pdfService.createPdf(byteArrayResource))
                .thenReturn(byteArrayResource);
        Mockito.doThrow(CommonMinioException.class).when(resultFileService)
                .save(ArgumentMatchers.any(), ArgumentMatchers.any());

        passportService.createPassportsPdf(generatePassportsDto);

        Mockito.verify(docxPassportService, Mockito.times(1)).createDocxPassports(generatePassportsDto);
        Mockito.verify(pdfService, Mockito.times(1)).createPdf(byteArrayResource);
        Mockito.verify(eventPublisher, Mockito.times(1)).publishErrorResultEvent(
                ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(resultFileService, Mockito.times(1)).save(ArgumentMatchers.any(),
                ArgumentMatchers.any());
        Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(ArgumentMatchers.any());
        Mockito.verify(kafkaBrokerSender, Mockito.never()).sendPassportInfoMessage(ArgumentMatchers.any());
    }

}
