package ru.veselov.transducersmanagingservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.transducersmanagingservice.validator.SortBySerialFieldValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for constraint checking sorting parameters for serial number
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortBySerialFieldValidator.class)
@Documented
public @interface SortBySerialField {

    String message() default "This value for parameter sort not exists or unsupported for sorting";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
