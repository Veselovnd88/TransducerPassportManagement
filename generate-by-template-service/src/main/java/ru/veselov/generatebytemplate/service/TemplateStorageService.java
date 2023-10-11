package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.dto.SortingParams;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.model.Template;

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
