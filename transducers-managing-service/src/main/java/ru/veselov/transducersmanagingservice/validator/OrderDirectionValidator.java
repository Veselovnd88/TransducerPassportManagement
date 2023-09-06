package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.veselov.transducersmanagingservice.annotation.OrderDirection;

import java.util.List;
@Slf4j
public class OrderDirectionValidator implements ConstraintValidator<OrderDirection, Object> {

    private List<String> availableOrderDirections;

    @Override
    public void initialize(OrderDirection constraintAnnotation) {
        availableOrderDirections = List.of("asc", "desc", "none");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String field = (String) value;
        log.debug("Validation of order direction sorting");
        return availableOrderDirections.contains(field);
    }

}
