package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.ru.INN;

@Data
public class CustomerDto {

    @NotBlank
    private String name;

    @NotBlank
    @INN
    private String inn;

    private String otherId;

}
