package ru.veselov.transducersmanagingservice.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SerialNumber {

    private String id;

    private String number;

    private String ptArt;

    private LocalDateTime createdAt;

}
