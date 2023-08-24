package ru.veselov.transducersmanagingservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/serials")
@Validated
@RequiredArgsConstructor
public class SerialNumberController {

    private final SerialNumberService serialNumberService;

    @GetMapping("/all/dates")
    public ResponseEntity<List<SerialNumber>> getAllSerialNumber(
            @SortingParam SortingParams sortingParams,
            @DateTimeFormat @RequestParam("after") LocalDate after,
            @DateTimeFormat @RequestParam("before") LocalDate before) {
        List<SerialNumber> serialNumbers = serialNumberService.findBetweenDates(sortingParams, after, before);
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }


}
