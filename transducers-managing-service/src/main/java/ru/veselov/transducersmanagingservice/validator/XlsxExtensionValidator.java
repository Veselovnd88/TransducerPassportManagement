package ru.veselov.transducersmanagingservice.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.transducersmanagingservice.annotation.Xlsx;

@Slf4j
public class XlsxExtensionValidator implements ConstraintValidator<Xlsx, Object> {

    private static final String FILE_EXT = ".xlsx";

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

    @Override
    public void initialize(Xlsx constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
