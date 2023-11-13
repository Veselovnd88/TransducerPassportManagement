package ru.veselov.generatebytemplate.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
@Tag("fast")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PassportServiceImplTest {
    //Mockito extends ArgumentMatchers so can user shorter methods names

    private static final ByteArrayResource byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);

    private static final String DATE_FORMAT = "dd-MM-yyyy";

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

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(passportService, "dateFormat", DATE_FORMAT, String.class);
    }

    @Test
    @Order(1)
    void shouldGenerateAndCreatePdfPassportsAndPublishEvent() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        ResultFile returnedResult = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto)).thenReturn(byteArrayResource);
        //or doReturn
        Mockito.when(pdfService.createPdf(Mockito.any())).thenReturn(byteArrayResource);
        Mockito.when(resultFileService.save(Mockito.any(), Mockito.any())).thenReturn(returnedResult);

        passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

        ArgumentCaptor<ResultFile> resultFileCaptor = ArgumentCaptor.forClass(ResultFile.class);
        Assertions.assertAll(
                () -> Mockito.verify(docxPassportService).createDocxPassports(generatePassportsDto),
                () -> Mockito.verify(pdfService).createPdf(byteArrayResource),
                () -> Mockito.verify(resultFileService)
                        .save(Mockito.any(ByteArrayResource.class), resultFileCaptor.capture()),
                () -> Mockito.verify(kafkaBrokerSender).sendPassportInfoMessage(generatePassportsDto),
                () -> Mockito.verify(eventPublisher).publishSuccessResultEvent(returnedResult)
        );
        ResultFile captured = resultFileCaptor.getValue();
        Assertions.assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(captured.getTaskId())
                        .isEqualTo(returnedResult.getTaskId()),
                () -> org.assertj.core.api.Assertions.assertThat(captured.getTemplateId())
                        .isEqualTo(returnedResult.getTemplateId()),
                () -> org.assertj.core.api.Assertions.assertThat(captured.getUsername())
                        .isEqualTo(returnedResult.getUsername())
        );
    }

    @Nested
    @DisplayName("Test error publishing due to exceptions")
    class ErrorTest {
        @Test
        @Order(2)
        void shouldPublishErrorEventWhenDocxExceptionHappened() {
            GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
            Mockito.doThrow(DocxProcessingException.class)
                    .when(docxPassportService).createDocxPassports(generatePassportsDto);

            passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

            Assertions.assertAll(
                    () -> Mockito.verify(eventPublisher).publishErrorResultEvent(Mockito.any(), Mockito.any()),
                    () -> Mockito.verifyNoInteractions(pdfService, resultFileService, kafkaBrokerSender),
                    () -> Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(Mockito.any())
            );
        }

        @Test
        @Order(3)
        void shouldPublishErrorEventWhenPdfExceptionHappened() {
            GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
            Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto)).thenReturn(byteArrayResource);
            Mockito.doThrow(PdfProcessingException.class).when(pdfService).createPdf(byteArrayResource);

            passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

            Assertions.assertAll(
                    () -> Mockito.verify(eventPublisher).publishErrorResultEvent(Mockito.any(), Mockito.any()),
                    () -> Mockito.verifyNoInteractions(resultFileService, kafkaBrokerSender),
                    () -> Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(Mockito.any())
            );
        }

        @Test
        @Order(4)
        void shouldPublishErrorEventWhenServiceUnavailableExceptionHappened() {
            GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
            Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto)).thenReturn(byteArrayResource);
            Mockito.doThrow(ServiceUnavailableException.class).when(pdfService).createPdf(byteArrayResource);

            passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

            Assertions.assertAll(
                    () -> Mockito.verify(eventPublisher).publishErrorResultEvent(Mockito.any(), Mockito.any()),
                    () -> Mockito.verifyNoInteractions(resultFileService, kafkaBrokerSender),
                    () -> Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(Mockito.any())
            );
        }

        @Test
        @Order(5)
        void shouldPublishErrorEventWhenTemplateNotFoundExceptionHappened() {
            GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
            Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto)).thenReturn(byteArrayResource);
            Mockito.when(pdfService.createPdf(byteArrayResource)).thenReturn(byteArrayResource);
            Mockito.doThrow(TemplateNotFoundException.class).when(resultFileService).save(Mockito.any(), Mockito.any());

            passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

            Assertions.assertAll(
                    () -> Mockito.verify(docxPassportService).createDocxPassports(generatePassportsDto),
                    () -> Mockito.verify(pdfService).createPdf(byteArrayResource),
                    () -> Mockito.verify(eventPublisher).publishErrorResultEvent(Mockito.any(), Mockito.any()),
                    () -> Mockito.verify(resultFileService).save(Mockito.any(), Mockito.any()),
                    () -> Mockito.verify(eventPublisher, Mockito.never()).publishSuccessResultEvent(Mockito.any()),
                    () -> Mockito.verifyNoInteractions(kafkaBrokerSender)
            );
        }

        @Test
        @Order(6)
        void shouldPublishErrorEventWhenMinioExceptionHappened() {
            GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
            Mockito.when(docxPassportService.createDocxPassports(generatePassportsDto)).thenReturn(byteArrayResource);
            Mockito.when(pdfService.createPdf(byteArrayResource)).thenReturn(byteArrayResource);
            Mockito.doThrow(CommonMinioException.class).when(resultFileService).save(Mockito.any(), Mockito.any());

            passportService.createPassportsPdf(generatePassportsDto,TestUtils.TASK_ID_STR, TestUtils.USERNAME);

            Assertions.assertAll(
                    () -> Mockito.verify(docxPassportService).createDocxPassports(generatePassportsDto),
                    () -> Mockito.verify(pdfService).createPdf(byteArrayResource),
                    () -> Mockito.verify(eventPublisher).publishErrorResultEvent(Mockito.any(), Mockito.any()),
                    () -> Mockito.verify(resultFileService).save(Mockito.any(), Mockito.any()),
                    () -> Mockito.verify(eventPublisher, Mockito.never())
                            .publishSuccessResultEvent(Mockito.any()),
                    () -> Mockito.verifyNoInteractions(kafkaBrokerSender)
            );
        }
    }

}
