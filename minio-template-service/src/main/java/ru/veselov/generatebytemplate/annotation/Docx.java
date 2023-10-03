package ru.veselov.generatebytemplate.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.generatebytemplate.validator.DocxExtensionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DocxExtensionValidator.class)
@Documented
public @interface Docx {
    String message() default "Document hasn't .docx extension";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
