package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;

public interface PassportStorageService {

    void save(GeneratePassportsDto generatePassportsDto);

    void deleteWithNullTemplateAndNullSerialNumber();

}
