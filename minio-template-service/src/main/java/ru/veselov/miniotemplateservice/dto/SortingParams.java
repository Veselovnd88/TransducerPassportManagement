package ru.veselov.miniotemplateservice.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.miniotemplateservice.annotation.OrderDirection;
import ru.veselov.miniotemplateservice.annotation.SortBy;

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
