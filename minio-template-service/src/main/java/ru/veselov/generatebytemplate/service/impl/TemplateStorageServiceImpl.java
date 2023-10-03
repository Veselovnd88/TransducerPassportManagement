package ru.veselov.generatebytemplate.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.generatebytemplate.dto.SortingParams;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.exception.PageExceedsMaximumValueException;
import ru.veselov.generatebytemplate.mapper.TemplateMapper;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.repository.TemplateRepository;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateStorageServiceImpl implements TemplateStorageService {

    public static final String TEMPLATE_WITH_ID_NOT_FOUND_LOG = "Template with [id: {}] not found";

    public static final String TEMPLATE_WITH_ID_NOT_FOUND = "Template with [id: %s] not found";

    @Value("${template.templates-per-page}")
    private int templatesPerPage;

    @Value("${scheduling.days-until-delete}")
    private int daysUntilDelete;

    private final TemplateRepository templateRepository;

    private final TemplateMapper templateMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TemplateEntity saveTemplateUnSynced(Template template) {
        TemplateEntity templateEntity = templateMapper.toEntity(template);
        templateEntity.setSynced(false);
        TemplateEntity saved = templateRepository.save(templateEntity);
        log.info("New [template:art-{}, name-{}] saved to repo", template.getPtArt(), template.getTemplateName());
        return saved;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncTemplate(UUID templateId) {
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(templateId);
        if (optionalTemplate.isPresent()) {
            TemplateEntity templateEntity = optionalTemplate.get();
            templateEntity.setSynced(true);
            templateRepository.save(templateEntity);
            log.info("Template successfully saved to MinIO and synced with DB");
        } else {
            log.error("Template with [id: {}] for sync not found", templateId);
            throw new EntityNotFoundException("Template with [id: %s] for sync not found".formatted(templateId));
        }

    }

    @Override
    public Template findTemplateById(String templateId) {
        UUID templateUUID = UUID.fromString(templateId);
        Optional<TemplateEntity> optionalTemplate = templateRepository.findById(templateUUID);
        if (optionalTemplate.isPresent()) {
            log.info("[Template: {}] retrieved from DB", templateId);
            return templateMapper.toModel(optionalTemplate.get());
        } else {
            log.error(TEMPLATE_WITH_ID_NOT_FOUND_LOG, templateId);
            throw new EntityNotFoundException(TEMPLATE_WITH_ID_NOT_FOUND.formatted(templateId));
        }
    }

    @Override
    public List<Template> findAll(SortingParams sortingParams) {
        long count = templateRepository.countAll();
        validatePageNumber(sortingParams.getPage(), count);
        Pageable pageable = createPageable(sortingParams.getPage(), sortingParams.getSort(), sortingParams.getOrder());
        Page<TemplateEntity> templatesPage = templateRepository.findAll(pageable);
        log.info("Retrieved list of templates with [sorting params: {}]", sortingParams);
        return templateMapper.toModels(templatesPage.getContent());
    }

    @Override
    public List<Template> findAllByPtArt(String ptArt, SortingParams sortingParams) {
        long count = templateRepository.countAllByPtArt(ptArt);
        validatePageNumber(sortingParams.getPage(), count);
        Pageable pageable = createPageable(sortingParams.getPage(), sortingParams.getSort(), sortingParams.getOrder());
        Page<TemplateEntity> templatePage = templateRepository.findAllByPtArt(ptArt, pageable);
        log.info("Retrieved list of templates with [ptArt: {} and sorting params: {}]", ptArt, sortingParams);
        return templateMapper.toModels(templatePage.getContent());
    }

    @Override
    @Transactional
    public void updateTemplate(String templateId) {
        UUID templateIdUUID = UUID.fromString(templateId);
        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findById(templateIdUUID);
        if (optionalTemplateEntity.isPresent()) {
            TemplateEntity templateEntity = optionalTemplateEntity.get();
            templateEntity.setEditedAt(LocalDateTime.now());
            templateRepository.save(templateEntity);
            log.info("Template [id: {}] info updated in DB", templateId);
        } else {
            log.error(TEMPLATE_WITH_ID_NOT_FOUND_LOG, templateId);
            throw new EntityNotFoundException(TEMPLATE_WITH_ID_NOT_FOUND.formatted(templateId));
        }
    }

    @Override
    @Transactional
    public void deleteTemplate(String templateId) {
        UUID templateIdUUID = UUID.fromString(templateId);
        Optional<TemplateEntity> optionalTemplateEntity = templateRepository.findById(templateIdUUID);
        if (optionalTemplateEntity.isPresent()) {
            TemplateEntity templateEntity = optionalTemplateEntity.get();
            templateRepository.delete(templateEntity);
            log.info("Template with [id: {}] deleted from DB", templateId);
        } else {
            log.error(TEMPLATE_WITH_ID_NOT_FOUND_LOG, templateId);
            throw new EntityNotFoundException(TEMPLATE_WITH_ID_NOT_FOUND.formatted(templateId));
        }
    }

    @Scheduled(cron = "${scheduling.delete-unsync}")
    @Transactional
    @Override
    public void deleteUnSynchronized() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(daysUntilDelete);
        templateRepository.deleteAllWithUnSyncFalse(deleteDate);
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
