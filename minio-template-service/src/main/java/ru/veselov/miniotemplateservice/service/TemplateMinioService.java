package ru.veselov.miniotemplateservice.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ru.veselov.miniotemplateservice.model.Template;

public interface TemplateMinioService {

    ByteArrayResource getTemplateByName(String name);

    void saveTemplate(Resource resource, Template template);

}
