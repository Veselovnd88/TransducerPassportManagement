package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;

public interface PassportService {

    void createPassportsPdf(GeneratePassportsDto generatePassportsDto, String taskId, String username);

}
