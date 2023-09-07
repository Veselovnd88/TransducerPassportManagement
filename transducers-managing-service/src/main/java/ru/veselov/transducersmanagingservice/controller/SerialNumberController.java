package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.transducersmanagingservice.annotation.DateParam;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.annotation.Xlsx;
import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;
import ru.veselov.transducersmanagingservice.validator.groups.SerialNumberField;

import java.util.List;

@RestController
@RequestMapping("/api/v1/serials")
@Validated({SerialNumberField.class, Default.class})
@RequiredArgsConstructor
public class SerialNumberController {

    private final SerialNumberService serialNumberService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void saveSerials(@Valid @RequestPart("serials") SerialsDto serialsDto,
                            @RequestPart("file") @Xlsx MultipartFile multipartFile) {
        serialNumberService.saveSerials(serialsDto, multipartFile);
    }

    @GetMapping("/all/dates")
    public List<SerialNumber> getAllSerialNumbersBetweenDates(
            @Valid @SortingParam SortingParams sortingParams,
            @DateParam DateParams dateParams) {
        return serialNumberService.findBetweenDates(sortingParams, dateParams);
    }

    @GetMapping("/all/dates/art/{ptArt}")
    public List<SerialNumber> getAllSerialNumbersByPtArtBetweenDates(@PathVariable("ptArt") String ptArt,
                                                                     @Valid @SortingParam SortingParams sortingParams,
                                                                     @DateParam DateParams dateParams) {
        return serialNumberService.findByPtArtBetweenDates(sortingParams, ptArt, dateParams);
    }

    @GetMapping("/number/{number}")
    public List<SerialNumber> getSerialNumberByNumber(@PathVariable("number") String number) {
        return serialNumberService.findByNumber(number);
    }

    @GetMapping("/all/art/{ptArt}")
    public List<SerialNumber> getAllSerialNumbersByPtArt(@PathVariable("ptArt") String ptArt,
                                                         @Valid @SortingParam SortingParams sortingParams) {
        return serialNumberService.findByArt(sortingParams, ptArt);
    }

    @GetMapping("/id/{serialId}")
    public SerialNumber getSerialNumberById(@UUID @PathVariable("serialId") String serialId) {
        return serialNumberService.findById(serialId);
    }

    @GetMapping("/all/dates/art/{ptArt}/customer/{customerId}")
    public List<SerialNumber> getAlLSerialNumberByPtArtAndCustomer(@PathVariable("ptArt") String ptArt,
                                                                   @UUID @PathVariable("customerId") String customerId,
                                                                   @Valid @SortingParam SortingParams sortingParams,
                                                                   @DateParam DateParams dateParams) {
        return serialNumberService
                .findByArtAndCustomerBetweenDates(sortingParams, ptArt, customerId, dateParams);
    }


    @DeleteMapping("/{serialId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteSerialNumberById(@UUID @PathVariable("serialId") String serialId) {
        serialNumberService.deleteSerial(serialId);
    }

}
