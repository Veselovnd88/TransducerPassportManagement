package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;

import java.util.List;

public interface SerialNumberService {

    void saveSerials(SerialsDto serialsDto);

    List<SerialNumber> findByNumber(String number);

    List<SerialNumber> findByArt(SortingParams sortingParams, String ptArt);

    List<SerialNumber> findBetweenDates(SortingParams sortingParams, DateParams dateParams);

    List<SerialNumber> findByPtArtBetweenDates(SortingParams sortingParams, String ptArt, DateParams dateParams);

    List<SerialNumber> findByArtAndCustomerBetweenDates(
            SortingParams sortingParams,
            String ptArt,
            String customerId,
            DateParams dateParams);

    SerialNumber findById(String serialId);

    void deleteSerial(String serialId);

}
