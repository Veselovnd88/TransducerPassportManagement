package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePassportsDto {

    @NotEmpty
    private List<SerialNumberDto> serials = new ArrayList<>();

    @NotNull
    @org.hibernate.validator.constraints.UUID
    private String templateId;

    @NotBlank
    private String ptArt;

    @NotNull
    private LocalDate printDate;

}
