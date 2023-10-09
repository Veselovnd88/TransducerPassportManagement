package ru.veselov.generatebytemplate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedResultFile {

    private UUID id;

    private String filename;

    private String bucket;

    @org.hibernate.validator.constraints.UUID
    private String templateId;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
