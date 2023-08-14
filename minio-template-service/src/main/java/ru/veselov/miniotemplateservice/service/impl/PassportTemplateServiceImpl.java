package ru.veselov.miniotemplateservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.mapper.TemplateMapper;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

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
        template.setFilename(generateFileName(templateInfo));
        templateMinioService.saveTemplate(resource, template);
        templateStorageService.saveTemplate(template);
    }

    private String generateFileName(TemplateDto templateDto) {
        return templateDto.getPtArt() + "-" + templateDto.getTemplateName() + FILE_EXT;
    }

}
