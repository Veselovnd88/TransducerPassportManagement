package ru.veselov.miniotemplateservice.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Template {

    private UUID id;

    private String ptArt;

    private String templateName;

    private String filename;

    private String bucket;

    private LocalDateTime editedAt;

    private LocalDateTime createdAt;

}
