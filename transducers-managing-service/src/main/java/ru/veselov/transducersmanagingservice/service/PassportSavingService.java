package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;

public interface PassportSavingService {

    void save(GeneratePassportsDto generatePassportsDto);

}
