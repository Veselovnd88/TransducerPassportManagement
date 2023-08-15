package ru.veselov.miniotemplateservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;
import ru.veselov.miniotemplateservice.model.Template;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TemplateMapper {

    TemplateEntity toEntity(Template template);

    Template toModel(TemplateEntity templateEntity);

    @Mapping(target = "filename", ignore = true)
    @Mapping(target = "templateName", ignore = true)
    Template dtoToTemplate(TemplateDto templateDto);

}
