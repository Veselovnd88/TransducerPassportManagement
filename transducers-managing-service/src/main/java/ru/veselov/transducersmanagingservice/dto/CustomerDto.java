package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    @NotBlank
    private String name;

    @NotBlank
    private String inn;

}
