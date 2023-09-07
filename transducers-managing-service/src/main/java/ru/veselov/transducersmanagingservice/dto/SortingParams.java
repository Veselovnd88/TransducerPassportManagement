package ru.veselov.transducersmanagingservice.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.veselov.transducersmanagingservice.annotation.OrderDirection;
import ru.veselov.transducersmanagingservice.annotation.SortByCustomerField;
import ru.veselov.transducersmanagingservice.annotation.SortBySerialField;
import ru.veselov.transducersmanagingservice.annotation.SortByTransducerField;
import ru.veselov.transducersmanagingservice.validator.groups.CustomerField;
import ru.veselov.transducersmanagingservice.validator.groups.SerialNumberField;
import ru.veselov.transducersmanagingservice.validator.groups.TransducerField;

@Data
@AllArgsConstructor
public class SortingParams {

    @PositiveOrZero(message = "Page number should be positive or zero")
    private Integer page;

    @SortBySerialField(groups = SerialNumberField.class)
    @SortByCustomerField(groups = CustomerField.class)
    @SortByTransducerField(groups = TransducerField.class)
    private String sort;

    @OrderDirection
    private String order;

}
