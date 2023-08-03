package ru.veselov.passportprocessing.service;

import java.util.List;

public interface PassportGeneratorService {

    byte[] generatePassports(List<String> serials, String templateId, String date);

}
