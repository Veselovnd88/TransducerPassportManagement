package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;

public interface PdfService {

    ByteArrayResource createPdf(byte[] source);

}
