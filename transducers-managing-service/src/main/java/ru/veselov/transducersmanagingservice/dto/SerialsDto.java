package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerialsDto {

    @NotBlank
    private String ptArt;

    private String comment;

    @NotBlank
    @UUID
    private String customerId;

    @DateTimeFormat
    private LocalDate savedAt;

}
