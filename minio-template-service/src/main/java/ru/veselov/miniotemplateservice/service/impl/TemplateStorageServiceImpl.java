package ru.veselov.miniotemplateservice.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.miniotemplateservice.dto.SortingParams;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.exception.PageExceedsMaximumValueException;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.repository.TemplateRepository;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateStorageServiceImpl implements TemplateStorageService {

    @Value("${template.templates-per-page}")
    private int templatesPerPage;

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
    public Template findTemplateById(String templateId) {
        UUID templateUUID = UUID.fromString(templateId);
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(templateUUID);
        if (optionalTemplate.isPresent()) {
            log.info("[Template: {}] retrieved from DB", templateId);
            return templateMapper.toModel(optionalTemplate.get());
        } else {
            log.error("Template with [id: {}] not found", templateId);
            throw new EntityNotFoundException("Template with [id: %s] not found".formatted(templateId));
        }
    }

    @Override
    public List<Template> findAll(SortingParams sortingParams) {
        long count = templateRepository.countAll();
        validatePageNumber(sortingParams.getPage(), count);
        Pageable pageable = createPageable(sortingParams.getPage(), sortingParams.getSort(), sortingParams.getOrder());
        Page<TemplateEntity> templatesPage = templateRepository.findAll(pageable);
        log.info("Retrieved list of templates with sorting params: {}", sortingParams);
        return templateMapper.toModels(templatesPage.getContent());
    }

    private Pageable createPageable(int page, String sort, String order) {
        Sort sortOrder;
        if (StringUtils.equals(order, "asc")) {
            sortOrder = Sort.by(sort).ascending();
        } else {
            sortOrder = Sort.by(sort).descending();
        }
        return PageRequest.of(page, templatesPerPage).withSort(sortOrder);
    }

    private void validatePageNumber(int page, long count) {
        long totalPages = count / templatesPerPage;
        if (page > totalPages) {
            log.error("Page number exceeds maximum value [max: {}, was: {}}]", totalPages, page);
            throw new PageExceedsMaximumValueException("Page number exceeds maximum value [max: %s, was: %s]"
                    .formatted(totalPages, page), page);
        }
    }

}
