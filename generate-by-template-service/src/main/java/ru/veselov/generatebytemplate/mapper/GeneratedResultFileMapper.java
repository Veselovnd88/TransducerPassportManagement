package ru.veselov.generatebytemplate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneratedResultFileMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "templateEntity", ignore = true)
    GeneratedResultFileEntity toEntity(GeneratedResultFile generatedResultFile);

    GeneratedResultFile toModel(GeneratedResultFileEntity generatedResultFileEntity);

}
