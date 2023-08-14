package ru.veselov.miniotemplateservice.service;

import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.model.Template;

import java.util.UUID;

public interface TemplateStorageService {

    void saveTemplate(Template template);

    TemplateEntity findTemplateById(UUID templateId);

}
