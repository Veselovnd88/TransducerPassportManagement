package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.service.PdfHttpClient;
import ru.veselov.generatebytemplate.service.PdfService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/*
 *Service responsible for sending bytes with generated docx to pdf converter, and return back pdf bytes
 * */
@Service
@Slf4j
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final PdfHttpClient pdfHttpClient;

    @Override
    public ByteArrayResource createPdf(ByteArrayResource source) {
        DataBuffer pdfDatabuffer = pdfHttpClient.sendRequestForConvertingDocxToPdf(source);
        log.info("Document successfully converted to pdf");
        return convertToByteArrayResource(pdfDatabuffer);
    }

    private ByteArrayResource convertToByteArrayResource(DataBuffer pdfDatabuffer) {
        if (pdfDatabuffer == null) {
            String errorMessage = "Pdf Service doesn't return correct byte array";
            log.error(errorMessage);
            throw new PdfProcessingException(errorMessage);
        }
        try (InputStream pdfInputStream = pdfDatabuffer.asInputStream();
             ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream()) {
            pdfInputStream.transferTo(pdfOutputStream);
            log.info("Pdf converted to byte array resource");
            return new ByteArrayResource(pdfOutputStream.toByteArray());
        } catch (IOException e) {
            String errorMessage = "Can't create byte array from pdf input stream";
            log.error(errorMessage + ": " + e.getMessage());
            throw new PdfProcessingException(errorMessage + ": " + e.getMessage());
        }
    }

}
