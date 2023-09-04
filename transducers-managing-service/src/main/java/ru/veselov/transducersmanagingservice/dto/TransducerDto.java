package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.veselov.transducersmanagingservice.entity.PressureType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransducerDto {
    @NotBlank
    private String art;

    private String transducerName;

    @NotBlank
    private String code;

    @NotNull
    private PressureType pressureType;

    @NotBlank
    private String model;

    @NotBlank
    private String outputCode;

    @NotBlank
    private String pressureRange;

    @NotBlank
    private String accuracy;

    @NotBlank
    private String electricalOutput;

    @NotBlank
    private String thread;

    @NotBlank
    private String connector;

    @NotBlank
    private String pinOut;
    @Max(value = 100)
    private String options;

}
