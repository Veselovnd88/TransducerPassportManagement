package ru.veselov.generatebytemplate.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import ru.veselov.generatebytemplate.model.Template;

public interface TemplateMinioService {

    ByteArrayResource getTemplateByName(String filename);

    void saveTemplate(Resource resource, Template template);

    void updateTemplate(Resource resource, Template template);

    void deleteTemplate(String filename);

}
