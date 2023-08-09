package ru.veselov.passportprocessing.service;

import ru.veselov.passportprocessing.dto.GeneratePassportsDto;
import ru.veselov.passportprocessing.entity.PassportEntity;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PassportStorageService {

    CompletableFuture<List<PassportEntity>> savePassports(GeneratePassportsDto generatePassportsDto);

}
