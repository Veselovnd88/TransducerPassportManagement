package ru.veselov.passportprocessing.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
public class SerialNumberDto {

    @NotBlank
    private String serial;

    @UUID
    private String serialId;

}
