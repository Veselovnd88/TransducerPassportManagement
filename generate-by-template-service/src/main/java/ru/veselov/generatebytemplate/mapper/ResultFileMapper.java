package ru.veselov.generatebytemplate.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;
import ru.veselov.generatebytemplate.model.ResultFile;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ResultFileMapper {

    @Mapping(target = "templateEntity", ignore = true)
    ResultFileEntity toEntity(ResultFile resultFile);

    ResultFile toModel(ResultFileEntity resultFileEntity);

}
