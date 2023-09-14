package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.transducersmanagingservice.annotation.SortByPassportField;

import java.util.List;

public class SortByPassportFieldValidator implements ConstraintValidator<SortByPassportField, Object> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortByPassportField constraintAnnotation) {
        availableSortFields = List.of("printDate", "templateId", "ptArt", "createdAt");
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
