package ru.veselov.miniotemplateservice.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;

public interface PassportTemplateService {

    void saveTemplate(MultipartFile file, TemplateDto templateInfo);

    ByteArrayResource getTemplate(String templateId);

    void updateTemplate(MultipartFile file, String templateId);

}
