package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.generatebytemplate.dto.TemplateDto;

public interface PassportTemplateService {

    void saveTemplate(MultipartFile file, TemplateDto templateInfo);

    ByteArrayResource getTemplate(String templateId);

    void updateTemplate(MultipartFile file, String templateId);

    void deleteTemplate(String templateId);

}
