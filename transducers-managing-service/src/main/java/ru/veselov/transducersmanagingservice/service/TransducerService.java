package ru.veselov.transducersmanagingservice.service;

import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.model.Transducer;

public interface TransducerService {

    Transducer saveTransducer(TransducerDto transducerDto);

}
