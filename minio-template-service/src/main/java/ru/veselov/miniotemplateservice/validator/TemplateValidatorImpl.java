package ru.veselov.miniotemplateservice.validator;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.exception.WrongFileExtensionException;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateValidatorImpl implements TemplateValidator {

    private static final String FILE_EXT = ".docx";

    private final TemplateRepository templateRepository;

    @Override
    public void validateTemplateName(String templateName) {
        Optional<TemplateEntity> templateOptional = templateRepository.findByName(templateName);
        if (templateOptional.isPresent()) {
            log.error("Template with [name: {}] already exists", templateName);
            throw new EntityExistsException("Template with name: %s already exists".formatted(templateName));
        }
    }

    @Override
    public void validateFileExtension(String filename) {
        if (!filename.toLowerCase().endsWith(FILE_EXT)) {
            log.error("File has no [{}] extension", FILE_EXT);
            throw new WrongFileExtensionException("File has no %s extension".formatted(FILE_EXT));
        }
    }
}
