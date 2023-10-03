package ru.veselov.generatebytemplate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.entity.TemplateEntity;
import ru.veselov.generatebytemplate.model.Template;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateMapper {

    TemplateEntity toEntity(Template template);

    List<Template> toModels(List<TemplateEntity> entities);

    Template toModel(TemplateEntity templateEntity);

    @Mapping(target = "filename", ignore = true)
    @Mapping(target = "templateName", ignore = true)
    Template dtoToTemplate(TemplateDto templateDto);

}
