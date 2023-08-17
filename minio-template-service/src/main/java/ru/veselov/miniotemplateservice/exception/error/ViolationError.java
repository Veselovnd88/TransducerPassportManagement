package ru.veselov.miniotemplateservice.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ViolationError implements Serializable {

    private String fieldName;

    private String message;

    private String currentValue;

}
