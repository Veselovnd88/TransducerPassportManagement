package ru.veselov.generatebytemplate.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePassportsDto {

    //TODO task num, username?

    @NotEmpty
    private List<SerialNumberDto> serials = new ArrayList<>();

    @NotNull
    @UUID
    private String templateId;

    @NotNull
    private LocalDate printDate;

}
