package ru.veselov.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialNumberDto {

    @NotBlank
    private String serial;

    @UUID
    private String serialId;

}
