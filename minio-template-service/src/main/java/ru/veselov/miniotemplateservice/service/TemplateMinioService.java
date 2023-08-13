package ru.veselov.miniotemplateservice.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ru.veselov.miniotemplateservice.dto.TemplateDto;

public interface TemplateMinioService {

    ByteArrayResource getTemplateByName(String name);

    void saveTemplate(Resource resource, TemplateDto templateInfo);

}
