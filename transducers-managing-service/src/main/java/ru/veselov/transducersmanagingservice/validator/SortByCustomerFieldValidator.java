package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.transducersmanagingservice.annotation.SortByCustomerField;

import java.util.List;

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
        return availableSortFields.contains(field);
    }

}
