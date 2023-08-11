package ru.veselov.miniotemplateservice.service;

import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.model.Template;

public interface TemplateStorageService {

    void saveTemplate(Template templateDto);
}
