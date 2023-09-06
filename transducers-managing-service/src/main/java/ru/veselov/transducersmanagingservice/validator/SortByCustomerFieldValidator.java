package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.veselov.transducersmanagingservice.annotation.SortByCustomerField;

import java.util.List;

@Slf4j
public class SortByCustomerFieldValidator implements ConstraintValidator<SortByCustomerField, Object> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortByCustomerField constraintAnnotation) {
        availableSortFields = List.of("createdAt", "name", "inn");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String field = (String) value;
        log.debug("Validation of sorting field for Customer");
        return availableSortFields.contains(field);
    }

}
