package ru.veselov.transducersmanagingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.model.Passport;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassportMapper {

    @Mapping(target = "templateId", source = "template.id")
    @Mapping(target = "serial", source = "serialNumber.number")
    @Mapping(target = "ptArt", source = "serialNumber.ptArt")
    Passport toModel(PassportEntity passportEntity);

    List<Passport> toModels(List<PassportEntity> entityList);

}
