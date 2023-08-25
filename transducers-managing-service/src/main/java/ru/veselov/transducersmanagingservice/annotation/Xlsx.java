package ru.veselov.transducersmanagingservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.transducersmanagingservice.validator.XlsxExtensionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XlsxExtensionValidator.class)
@Documented
public @interface Xlsx {
    String message() default "Document hasn't .xlsx extension";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}