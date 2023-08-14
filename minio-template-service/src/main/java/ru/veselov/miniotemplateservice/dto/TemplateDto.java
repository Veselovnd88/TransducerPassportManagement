package ru.veselov.miniotemplateservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDto {

    private String templateDescription;

    @NotNull
    private String ptArt;

    @NotNull
    private String bucket;

}
