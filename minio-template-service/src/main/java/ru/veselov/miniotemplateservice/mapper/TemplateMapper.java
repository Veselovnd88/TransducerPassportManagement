package ru.veselov.miniotemplateservice.mapper;

import org.mapstruct.Mapper;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.model.Template;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    TemplateEntity toEntity(Template template);

}
