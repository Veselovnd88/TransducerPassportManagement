package ru.veselov.transducersmanagingservice.model;

import lombok.Data;
import ru.veselov.transducersmanagingservice.entity.PressureType;

import java.time.LocalDateTime;

@Data
public class Transducer {

    private String id;

    private String art;

    private String name;

    private PressureType pressureType;

    private String model;

    private String outputCode;

    private String pressureRange;

    private String accuracy;

    private String electricalOutput;

    private String thread;

    private String connector;

    private String pinOut;

    private String options;

    private LocalDateTime createdAt;

}
