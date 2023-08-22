package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.transducersmanagingservice.annotation.SortBy;

import java.util.List;

public class SortByFieldValidator implements ConstraintValidator<SortBy, Object> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortBy constraintAnnotation) {
        availableSortFields = List.of("createdAt", "number", "ptArt");
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
