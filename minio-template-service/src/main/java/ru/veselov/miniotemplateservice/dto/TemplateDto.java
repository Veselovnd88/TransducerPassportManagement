package ru.veselov.miniotemplateservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateDto {

    @NotNull
    private String ptArt;

    @NotNull
    private String bucket;

}
