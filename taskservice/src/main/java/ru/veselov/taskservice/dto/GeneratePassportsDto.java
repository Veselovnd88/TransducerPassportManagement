package ru.veselov.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
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

    @NotEmpty
    private List<SerialNumberDto> serials = new ArrayList<>();

    @NotNull
    @UUID
    private String templateId;

    @NotNull
    private LocalDate printDate;

    @NotBlank
    private String username;

    @NotNull
    @UUID
    private String taskId;

}
