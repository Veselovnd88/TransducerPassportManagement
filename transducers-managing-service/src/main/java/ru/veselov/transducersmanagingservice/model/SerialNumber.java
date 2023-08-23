package ru.veselov.transducersmanagingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SerialNumber {

    private String id;

    private String number;

    private String ptArt;

    @JsonFormat(pattern = "yyyy-mm-dd")
    private LocalDate savedAt;

    @JsonFormat(pattern = "yyyy-mm-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
