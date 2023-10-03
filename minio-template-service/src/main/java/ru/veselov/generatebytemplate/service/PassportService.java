package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;

public interface PassportService {

    byte[] createPassportsPdf(GeneratePassportsDto generatePassportsDto);

}
