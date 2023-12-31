package ru.veselov.transducersmanagingservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SerialNumber {

    private String id;

    private String number;

    private String ptArt;

    private String comment;

    private String customer;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate savedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

}
