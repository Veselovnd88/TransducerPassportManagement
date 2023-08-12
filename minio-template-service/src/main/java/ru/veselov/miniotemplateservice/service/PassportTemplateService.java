package ru.veselov.miniotemplateservice.service;

import org.springframework.web.multipart.MultipartFile;
import ru.veselov.miniotemplateservice.dto.TemplateDto;

public interface PassportTemplateService {

    void saveTemplate(MultipartFile file, TemplateDto templateInfo);
}
