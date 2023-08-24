package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.transducersmanagingservice.annotation.DateParam;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/serials")
@Validated
@RequiredArgsConstructor
public class SerialNumberController {

    private final SerialNumberService serialNumberService;

    @PostMapping
    public ResponseEntity<Void> saveSerials(@Valid SerialsDto serialsDto) {
        serialNumberService.saveSerials(serialsDto);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/all/dates")
    public ResponseEntity<List<SerialNumber>> getAllSerialNumbersBetweenDates(
            @SortingParam SortingParams sortingParams,
            @DateParam DateParams dateParams) {
        List<SerialNumber> serialNumbers = serialNumberService.findBetweenDates(sortingParams, dateParams);
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }

    @GetMapping("/all/dates/{ptArt}")
    public ResponseEntity<List<SerialNumber>> getAllSerialNumbersByPtArtBetweenDates(
            @PathVariable("ptArt") String ptArt,
            @SortingParam SortingParams sortingParams,
            @DateParam DateParams dateParams) {
        List<SerialNumber> serialNumbers = serialNumberService
                .findByPtArtBetweenDates(sortingParams, ptArt, dateParams);
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }

    @GetMapping("/{number}")
    public ResponseEntity<List<SerialNumber>> getSerialNumberByNumber(@PathVariable("number") String number) {
        List<SerialNumber> serialNumbers = serialNumberService.findByNumber(number);
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }

    @GetMapping("/all/{ptArt}")
    public ResponseEntity<List<SerialNumber>> getAllSerialNumbersByPtArt(
            @PathVariable("ptArt") String ptArt,
            @SortingParam SortingParams sortingParams) {
        List<SerialNumber> serialNumbers = serialNumberService.findByArt(sortingParams, ptArt);
        return new ResponseEntity<>(serialNumbers, HttpStatus.OK);
    }

    @GetMapping("/{serialId}")
    public ResponseEntity<SerialNumber> getSerialNumberById(@UUID @PathVariable("serialId") String serialId) {
        SerialNumber serialNumber = serialNumberService.findById(serialId);
        return new ResponseEntity<>(serialNumber, HttpStatus.OK);
    }

    @DeleteMapping("/{serialId}")
    public ResponseEntity<Void> deleteSerialNumberById(@UUID @PathVariable("serialId") String serialId) {
        serialNumberService.deleteSerial(serialId);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

}
