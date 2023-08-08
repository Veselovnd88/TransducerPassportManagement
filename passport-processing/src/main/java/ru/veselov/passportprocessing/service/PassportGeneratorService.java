package ru.veselov.passportprocessing.service;

import org.springframework.core.io.ByteArrayResource;

import java.util.List;

public interface PassportGeneratorService {

    byte[] generatePassports(List<String> serials, ByteArrayResource templateByteArrayResource, String date);

}
