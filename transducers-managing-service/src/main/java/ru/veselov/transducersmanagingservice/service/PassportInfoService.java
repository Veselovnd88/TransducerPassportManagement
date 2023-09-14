package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.Passport;

import java.util.List;

public interface PassportInfoService {

    Passport getById(String passportId);

    List<Passport> getAllBetweenDates(SortingParams sortingParams, DateParams dateParams);

    List<Passport> getAllForSerialBetweenDates(String serialNumber, SortingParams sortingParams, DateParams dateParams);

    List<Passport> getAllForPtArtBetweenDates(String ptArt, SortingParams sortingParams, DateParams dateParams);

    void deleteById(String passportId);

}
