package ru.veselov.generatebytemplate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.generatebytemplate.annotation.Docx;

@Slf4j
public class DocxExtensionValidator implements ConstraintValidator<Docx, Object> {

    private static final String FILE_EXT = ".docx";

    @Override
    public void initialize(Docx constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        log.debug("Validation of Multipart file is started");
        MultipartFile multipartFile = (MultipartFile) value;
        if (multipartFile.getResource().getFilename() == null) {
            log.error("Filename is null");
            return false;
        }
        return multipartFile.getResource().getFilename().toLowerCase().endsWith(FILE_EXT);
    }

}
