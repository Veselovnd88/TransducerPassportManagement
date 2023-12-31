package ru.veselov.generatebytemplate.validator;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.repository.TemplateRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateValidatorImpl implements TemplateValidator {

    private final TemplateRepository templateRepository;

    @Override
    public void validateTemplateName(String templateName) {
        Optional<TemplateEntity> templateOptional = templateRepository.findByName(templateName);
        if (templateOptional.isPresent()) {
            log.error("Template with [name: {}] already exists", templateName);
            throw new EntityExistsException("Template with name: %s already exists".formatted(templateName));
        }
    }

}
