package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.transducersmanagingservice.annotation.DateParam;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.Passport;
import ru.veselov.transducersmanagingservice.service.PassportInfoService;
import ru.veselov.transducersmanagingservice.validator.groups.PassportField;

import java.util.List;

@RestController
@RequestMapping("/api/v1/passport")
@RequiredArgsConstructor
@Validated({PassportField.class, Default.class})
public class PassportInfoController {

    private final PassportInfoService passportInfoService;

    @GetMapping("/id/{passportId}")
    public Passport getPassportById(@PathVariable("passportId") @UUID String passportId) {
        return passportInfoService.getById(passportId);
    }

    @GetMapping("/all")
    public List<Passport> getAllPassports(@SortingParam SortingParams sortingParams,
                                          @DateParam DateParams dateParams) {
        return passportInfoService.getAllBetweenDates(sortingParams, dateParams);
    }

    @GetMapping("/all/serial/{serialNumber}")
    public List<Passport> getAllPassportsForSerial(@PathVariable("serialNumber") String serialNumber,
                                                   @SortingParam SortingParams sortingParams,
                                                   @DateParam DateParams dateParams) {
        return passportInfoService.getAllForSerialBetweenDates(serialNumber, sortingParams, dateParams);
    }

    @GetMapping("/all/ptArt/{ptArt}")
    public List<Passport> getAllPassportsForPtArt(@PathVariable("ptArt") String ptArt,
                                                  @SortingParam SortingParams sortingParams,
                                                  @DateParam DateParams dateParams) {
        return passportInfoService.getAllForPtArtBetweenDates(ptArt, sortingParams, dateParams);
    }

    @DeleteMapping("/id/{passportId}")
    public void deletePassport(@PathVariable("passportId") @UUID String passportId) {
        passportInfoService.deleteById(passportId);
    }

}
