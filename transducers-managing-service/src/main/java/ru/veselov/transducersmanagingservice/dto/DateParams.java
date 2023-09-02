package ru.veselov.transducersmanagingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DateParams {

    private LocalDate after;

    private LocalDate before;

}
