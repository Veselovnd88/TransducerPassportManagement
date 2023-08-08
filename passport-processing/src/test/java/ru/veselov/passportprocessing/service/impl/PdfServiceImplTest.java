package ru.veselov.passportprocessing.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import ru.veselov.passportprocessing.service.PdfHttpClient;

import java.io.ByteArrayInputStream;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    @Mock
    PdfHttpClient pdfHttpClient;

    @InjectMocks
    PdfServiceImpl pdfService;

    @Test
    void shouldReturnByteArray() {
        DataBuffer mockDataBuffer = Mockito.mock(DataBuffer.class);
        Mockito.when(mockDataBuffer.asInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3, 4}));
        Mockito.when(pdfHttpClient.sendRequestForConvertingDocxToPdf(ArgumentMatchers.any()))
                .thenReturn(mockDataBuffer);
        byte[] pdfBytes = pdfService.createPdf(new byte[]{1, 2});

        Assertions.assertThat(pdfBytes).isNotNull();
    }

}