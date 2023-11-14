package ru.veselov.taskservice.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViolationError implements Serializable {

    private String fieldName;

    private String message;

    private String currentValue;

}
