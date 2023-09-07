package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.model.Transducer;

import java.util.List;

public interface TransducerService {

    Transducer saveTransducer(TransducerDto transducerDto);

    Transducer findTransducerById(String transducerId);

    Transducer findTransducerByArt(String ptArt);

    void deleteTransducer(String transducerId);

    Transducer updateTransducer(String transducerId, TransducerDto transducerDto);

    List<Transducer> getAll(SortingParams sortingParams);

}
