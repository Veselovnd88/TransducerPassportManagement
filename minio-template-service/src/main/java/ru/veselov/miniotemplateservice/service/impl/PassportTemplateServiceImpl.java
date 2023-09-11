package ru.veselov.miniotemplateservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

/**
 * Service for managing templates of passports
 * Docx templates saved in minio storage, metadata and information saved in DB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

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

    @Override
    public ByteArrayResource getTemplate(String templateId) {
        Template templateById = templateStorageService.findTemplateById(templateId);
        ByteArrayResource templateBytes = templateMinioService.getTemplateByName(templateById.getFilename());
        log.info("Template source bytes with [id: {}] retrieved from storage", templateId);
        return templateBytes;
    }

    @Override
    public void updateTemplate(MultipartFile file, String templateId) {
        Template template = templateStorageService.findTemplateById(templateId);
        templateMinioService.updateTemplate(file.getResource(), template);
        templateStorageService.updateTemplate(templateId);
        log.info("Template for [id: {}] successfully updated", templateId);
    }

    @Override
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
