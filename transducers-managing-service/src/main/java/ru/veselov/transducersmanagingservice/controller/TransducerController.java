package ru.veselov.transducersmanagingservice.controller;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.veselov.transducersmanagingservice.annotation.SortingParam;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.model.Transducer;
import ru.veselov.transducersmanagingservice.service.TransducerService;
import ru.veselov.transducersmanagingservice.validator.groups.TransducerField;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transducer")
@Validated({TransducerField.class, Default.class})
@RequiredArgsConstructor
public class TransducerController {

    private final TransducerService transducerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Transducer createTransducer(@Valid @RequestBody TransducerDto transducerDto) {
        return transducerService.saveTransducer(transducerDto);
    }

    @GetMapping("/id/{transducerId}")
    public Transducer getById(@PathVariable("transducerId") @UUID String transducerId) {
        return transducerService.findTransducerById(transducerId);
    }

    @GetMapping("/art/{ptArt}")
    public Transducer getByArt(@PathVariable("ptArt") String ptArt) {
        return transducerService.findTransducerByArt(ptArt);
    }

    @GetMapping("/all")
    public List<Transducer> getAll(@SortingParam @Valid SortingParams sortingParams) {
        return transducerService.getAll(sortingParams);
    }

    @DeleteMapping("/delete/{transducerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deleteTransducer(@PathVariable("transducerId") @UUID String transducerId) {
        transducerService.deleteTransducer(transducerId);
    }

    @PutMapping("/update/{transducerId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Transducer updateTransducer(@PathVariable("transducerId") @UUID String transducerId,
                                       @RequestBody @Valid TransducerDto transducerDto) {
        return transducerService.updateTransducer(transducerId, transducerDto);
    }

}
