package ru.veselov.generatebytemplate.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.generatebytemplate.annotation.OrderDirection;
import ru.veselov.generatebytemplate.annotation.SortBy;

@Data
@AllArgsConstructor
public class SortingParams {

    @PositiveOrZero(message = "Page number should be positive or zero")
    private Integer page;

    @SortBy
    private String sort;

    @OrderDirection
    private String order;

}
