package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;

import java.time.LocalDate;
import java.util.List;

public interface SerialNumberService {

    void saveSerials(List<String> serials, String ptArt);

    List<SerialNumber> findByNumber(String number);

    List<SerialNumber> findByArt(SortingParams sortingParams,String ptArt);

    List<SerialNumber> findByDate(SortingParams sortingParams, LocalDate before, LocalDate after);

    void deleteSerial(String serialId);

}
