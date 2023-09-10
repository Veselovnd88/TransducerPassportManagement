package ru.veselov.miniotemplateservice.service;

import ru.veselov.miniotemplateservice.dto.SortingParams;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.model.Template;

import java.util.List;
import java.util.UUID;

public interface TemplateStorageService {

    TemplateEntity saveTemplateUnSynced(Template template);

    void syncTemplate(UUID templateId);

    Template findTemplateById(String templateId);

    List<Template> findAll(SortingParams sortingParams);

    List<Template> findAllByPtArt(String ptArt, SortingParams sortingParams);

    void updateTemplate(String templateId);

    void deleteTemplate(String templateId);

}
