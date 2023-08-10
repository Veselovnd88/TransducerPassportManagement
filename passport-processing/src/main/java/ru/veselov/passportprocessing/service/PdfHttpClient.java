package ru.veselov.passportprocessing.service;

import org.springframework.core.io.buffer.DataBuffer;

public interface PdfHttpClient {

    DataBuffer sendRequestForConvertingDocxToPdf(byte[] source);

}
