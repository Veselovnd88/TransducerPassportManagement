package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import ru.veselov.transducersmanagingservice.annotation.SortBySerialField;

import java.util.List;
@Slf4j
public class SortBySerialFieldValidator implements ConstraintValidator<SortBySerialField, Object> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortBySerialField constraintAnnotation) {
        availableSortFields = List.of("createdAt", "number", "ptArt");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String field = (String) value;
        log.debug("Validation of soring field for serialNumber");
        return availableSortFields.contains(field);
    }

}
