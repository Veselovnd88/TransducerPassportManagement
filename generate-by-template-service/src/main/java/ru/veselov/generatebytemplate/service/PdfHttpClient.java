package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;

public interface PdfHttpClient {

    DataBuffer sendRequestForConvertingDocxToPdf(ByteArrayResource source);

}
