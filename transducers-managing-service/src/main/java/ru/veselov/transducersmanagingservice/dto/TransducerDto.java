package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.veselov.transducersmanagingservice.entity.PressureType;

@Data
public class TransducerDto {
    @NotNull
    private String art;

    private String name;
    @NotNull
    //@PressureType
    private PressureType pressureType;

    @NotNull
    private String model;

    @NotNull
    private String outputCode;

    @NotNull
    private String pressureRange;

    @NotNull
    private String accuracy;

    @NotNull
    private String electricalOutput;

    @NotNull
    private String thread;

    @NotNull
    private String connector;

    @NotNull
    private String pinOut;

    private String options;

}
