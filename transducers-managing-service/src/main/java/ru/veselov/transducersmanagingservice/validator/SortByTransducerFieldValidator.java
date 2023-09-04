package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.transducersmanagingservice.annotation.SortByTransducerField;

import java.util.ArrayList;
import java.util.List;

public class SortByTransducerFieldValidator implements ConstraintValidator<SortByTransducerField, String> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortByTransducerField constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        availableSortFields = new ArrayList<>(
                List.of("art", "id", "model", "createdAt", "pressureType", "accuracy", "thread", "connector"));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return availableSortFields.contains(value);
    }
}
