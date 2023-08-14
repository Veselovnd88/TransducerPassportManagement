package ru.veselov.miniotemplateservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateStorageServiceImpl implements TemplateStorageService {

    private final TemplateRepository templateRepository;

    private final TemplateMapper templateMapper;

    private final TemplateValidator templateValidator;

    @Override
    @Transactional
    public void saveTemplate(Template template) {
        templateValidator.validateTemplateName(template.getTemplateName());
        TemplateEntity templateEntity = templateMapper.toEntity(template);
        templateRepository.save(templateEntity);
        log.info("New [template:art-{}, name-{}] saved to repo", template.getPtArt(), template.getTemplateName());
    }

    @Override
    public TemplateEntity findTemplateById(UUID templateId) {//TODO TESTME
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(templateId);
        return optionalTemplate.orElseThrow(() -> {
            log.error("Template with [id: {}] not found", templateId);
            throw new EntityNotFoundException("Template with [id: %s] not found".formatted(templateId));
        });
    }

}
