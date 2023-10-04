package ru.veselov.generatebytemplate.service.impl;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.service.PdfHttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    public final static byte[] SOURCE = new byte[]{1, 2, 3, 4};

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
        byte[] pdfBytes = pdfService.createPdf(SOURCE);

        Assertions.assertThat(pdfBytes).isNotNull();
    }

    @Test
    void shouldThrowExceptionIfDataBufferIsNull() {
        Mockito.when(pdfHttpClient.sendRequestForConvertingDocxToPdf(ArgumentMatchers.any()))
                .thenReturn(null);

        Assertions.assertThatThrownBy(() -> pdfService.createPdf(SOURCE))
                .isInstanceOf(PdfProcessingException.class);
    }

    @Test
    @SneakyThrows
    void shouldThrowExceptionIfCantConvertToPdfBytes() {
        DataBuffer mockDataBuffer = Mockito.mock(DataBuffer.class);
        InputStream inputStream = Mockito.mock(InputStream.class);
        Mockito.when(inputStream.transferTo(ArgumentMatchers.any())).thenThrow(IOException.class);
        Mockito.when(mockDataBuffer.asInputStream()).thenReturn(inputStream);
        Mockito.when(pdfHttpClient.sendRequestForConvertingDocxToPdf(ArgumentMatchers.any()))
                .thenReturn(mockDataBuffer);

        Assertions.assertThatThrownBy(() -> pdfService.createPdf(SOURCE))
                .isInstanceOf(PdfProcessingException.class);
        inputStream.close();
    }

}
