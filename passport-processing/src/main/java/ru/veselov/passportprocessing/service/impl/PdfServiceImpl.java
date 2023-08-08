package ru.veselov.passportprocessing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import ru.veselov.passportprocessing.exception.PdfProcessingException;
import ru.veselov.passportprocessing.service.PdfHttpClient;
import ru.veselov.passportprocessing.service.PdfService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final PdfHttpClient pdfHttpClient;

    @Override
    public byte[] createPdf(byte[] source) {
        DataBuffer pdfDatabuffer = pdfHttpClient.sendRequestForConvertingDocxToPdf(source);
        log.info("Document successfully converted to pdf");
        return convertToByteArray(pdfDatabuffer);
    }

    private byte[] convertToByteArray(DataBuffer pdfDatabuffer) {
        if (pdfDatabuffer == null) {
            String errorMessage = "Pdf Service doesn't return correct byte array";
            log.error(errorMessage);
            throw new PdfProcessingException(errorMessage);
        }
        try (InputStream pdfInputStream = pdfDatabuffer.asInputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            pdfInputStream.transferTo(pdfOutputStream);
            log.info("Pdf converted to byte array");
            return pdfOutputStream.toByteArray();
        } catch (IOException e) {
            String errorMessage = "Can't create byte array from pdf input stream";
            log.error(errorMessage + ": " + e.getMessage());
            throw new PdfProcessingException(errorMessage + ": " + e.getMessage());
        }
    }

}
