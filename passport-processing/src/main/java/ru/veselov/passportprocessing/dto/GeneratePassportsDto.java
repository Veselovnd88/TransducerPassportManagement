package ru.veselov.passportprocessing.dto;

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
    //TODO validation and validation aspect

    @NotEmpty
    private List<String> serials = new ArrayList<>();

    @NotNull
    private String templateId;


    private LocalDate date;

}
