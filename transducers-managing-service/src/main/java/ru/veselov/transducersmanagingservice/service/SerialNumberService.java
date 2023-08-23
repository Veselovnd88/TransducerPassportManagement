package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SerialNumberService {

    void saveSerials(List<String> serials, String ptArt);

    List<SerialNumber> findByNumber(String number);

    List<SerialNumber> findByArt(SortingParams sortingParams, String ptArt);

    List<SerialNumber> findBetweenDates(SortingParams sortingParams, LocalDate before, LocalDate after);

    List<SerialNumber> findByPtArtBetweenDates(SortingParams sortingParams, String ptArt, LocalDate before,
                                               LocalDate after);

    Optional<SerialNumber> findById(String serialId);

    void deleteSerial(String serialId);

}
