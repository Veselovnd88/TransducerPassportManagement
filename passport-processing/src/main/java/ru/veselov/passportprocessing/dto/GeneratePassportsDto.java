package ru.veselov.passportprocessing.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePassportsDto {
    //TODO validation and validation aspect

    @NotEmpty
    private List<String> serials = new ArrayList<>();

    @NotNull
    private UUID templateId;

    private String ptArt;

    private LocalDate date;

}
