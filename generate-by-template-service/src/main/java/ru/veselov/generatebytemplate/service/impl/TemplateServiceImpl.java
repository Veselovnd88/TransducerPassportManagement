package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.mapper.TemplateMapper;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.PassportTemplateService;
import ru.veselov.generatebytemplate.service.TemplateMinioService;
import ru.veselov.generatebytemplate.service.TemplateStorageService;
import ru.veselov.generatebytemplate.validator.TemplateValidator;

/**
 * Service for managing templates of passports
 * Docx templates saved in minio storage, metadata and information saved in DB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements PassportTemplateService {

    private static final String FILE_EXT = ".docx";

    private final TemplateMinioService templateMinioService;

    private final TemplateStorageService templateStorageService;

    private final TemplateValidator templateValidator;

    private final TemplateMapper templateMapper;

    @Override
    public void saveTemplate(MultipartFile file, TemplateDto templateInfo) {
        templateValidator.validateTemplateName(generateTemplateName(templateInfo));
        Resource resource = file.getResource();
        Template template = templateMapper.dtoToTemplate(templateInfo);
        template.setTemplateName(generateTemplateName(templateInfo));
        template.setFilename(generateFileName(templateInfo));
        //saved with sync=false
        TemplateEntity templateEntity = templateStorageService.saveTemplateUnSynced(template);
        //upload template
        templateMinioService.saveTemplate(resource, template);
        //update with sync=true
        templateStorageService.syncTemplate(templateEntity.getId());
        log.info("Template [art: {}, name: {}] saved to MinIO storage and to DB",
                templateInfo.getPtArt(), templateInfo.getTemplateDescription());
    }

    @Cacheable(value = "template")
    @Override
    public ByteArrayResource getTemplate(String templateId) {
        Template templateById = templateStorageService.findTemplateById(templateId);
        ByteArrayResource templateBytes = templateMinioService.getTemplateByName(templateById.getFilename());
        log.info("Template source bytes with [id: {}] retrieved from storage", templateId);
        return templateBytes;
    }

    @CacheEvict(value = "template", key = "#templateId")
    @Override
    public void updateTemplate(MultipartFile file, String templateId) {
        Template template = templateStorageService.findTemplateById(templateId);
        templateMinioService.updateTemplate(file.getResource(), template);
        templateStorageService.updateTemplate(templateId);
        log.info("Template for [id: {}] successfully updated", templateId);
    }

    @Override
    @CacheEvict(value = "template", key = "#templateId")
    public void deleteTemplate(String templateId) {
        Template template = templateStorageService.findTemplateById(templateId);
        String filename = template.getFilename();
        templateMinioService.deleteTemplate(filename);
        templateStorageService.deleteTemplate(templateId);
        log.info("Template for [id: {}] successfully deleted", templateId);
    }

    private String generateFileName(TemplateDto templateDto) {
        return generateTemplateName(templateDto) + FILE_EXT;
    }

    private String generateTemplateName(TemplateDto templateDto) {
        return templateDto.getPtArt() + "-" + templateDto.getTemplateDescription();
    }

}
