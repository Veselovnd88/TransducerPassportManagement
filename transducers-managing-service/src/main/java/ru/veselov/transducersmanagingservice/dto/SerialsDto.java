package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialsDto {
    @NotEmpty
    private List<String> serials = new ArrayList<>();

    @NotBlank
    private String ptArt;

    private String comment;

    @NotBlank
    private String customer;

    @DateTimeFormat
    private LocalDate savedAt;

}
