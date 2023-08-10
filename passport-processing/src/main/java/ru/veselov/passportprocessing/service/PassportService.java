package ru.veselov.passportprocessing.service;

import ru.veselov.passportprocessing.dto.GeneratePassportsDto;

public interface PassportService {

    byte[] createPassportsPdf(GeneratePassportsDto generatePassportsDto);
}
