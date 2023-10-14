package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface DocxGeneratorService {

    byte[] generateDocx(List<String> serials, ByteArrayResource templateByteArrayResource, String date);

}
