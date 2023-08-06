package ru.veselov.passportprocessing.service;

import java.util.List;

public interface PassportService {

    byte[] createPassportsPdf(List<String> serials, String templateId, String date);
}
