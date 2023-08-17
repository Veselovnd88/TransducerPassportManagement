package ru.veselov.miniotemplateservice.service;

import ru.veselov.miniotemplateservice.dto.SortingParams;
import ru.veselov.miniotemplateservice.model.Template;

import java.util.List;
import java.util.Optional;

public interface TemplateStorageService {

    void saveTemplate(Template template);

    Template findTemplateById(String templateId);

    List<Template> findAll(SortingParams sortingParams);

    List<Template> findAllByPtArt(String ptArt, SortingParams sortingParams);

    Template updateTemplate(String templateId);

    Optional<Template> deleteTemplate(String templateId);

}
