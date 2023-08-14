package ru.veselov.miniotemplateservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PassportTemplateServiceImpl implements PassportTemplateService {

    private static final String FILE_EXT = ".docx";

    private final TemplateMinioService templateMinioService;

    private final TemplateStorageService templateStorageService;

    private final TemplateMapper templateMapper;

    @Override
    @Transactional
    public void saveTemplate(MultipartFile file, TemplateDto templateInfo) {
        Resource resource = file.getResource();
        Template template = templateMapper.dtoToTemplate(templateInfo);
        template.setTemplateName(generateTemplateName(templateInfo));
        template.setFilename(generateFileName(templateInfo));
        templateStorageService.saveTemplate(template);
        templateMinioService.saveTemplate(resource, template);
        log.info("Template [art: {}, name: {}] saved to MinIO storage and to DB",
                templateInfo.getPtArt(), templateInfo.getTemplateDescription());
    }

    @Override
    public ByteArrayResource getTemplate(String templateId) {//TODO TESTME
        UUID templateUUID = UUID.fromString(templateId);
        TemplateEntity templateById = templateStorageService.findTemplateById(templateUUID);
        log.info("Information for [template: {}] retrieved from DB", templateById);
        ByteArrayResource templateBytes = templateMinioService.getTemplateByName(templateById.getFilename());
        log.info("Template source bytes with [id: {}] retrieved from storage", templateId);
        return templateBytes;
    }


    private String generateFileName(TemplateDto templateDto) {
        return generateTemplateName(templateDto) + FILE_EXT;
    }

    private String generateTemplateName(TemplateDto templateDto) {
        return templateDto.getPtArt() + "-" + templateDto.getTemplateDescription();
    }

}
