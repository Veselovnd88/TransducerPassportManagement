package ru.veselov.passportprocessing.service;

import java.io.InputStream;
import java.util.List;

public interface PassportGeneratorService {

    byte[] generatePassports(List<String> serials, InputStream templateInputStream, String date);

}
