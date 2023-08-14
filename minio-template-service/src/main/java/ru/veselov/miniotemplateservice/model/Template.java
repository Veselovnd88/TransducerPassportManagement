package ru.veselov.miniotemplateservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template {

    private UUID id;

    private String ptArt;

    private String templateName;

    private String filename;

    private String bucket;

    private LocalDateTime editedAt;

    private LocalDateTime createdAt;

}
