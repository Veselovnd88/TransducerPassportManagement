package ru.veselov.transducersmanagingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.veselov.passportprocessing.entity.PassportEntity;
import ru.veselov.passportprocessing.model.Passport;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PassportMapper {

    Passport toModel(PassportEntity passportEntity);

    List<Passport> toModels(List<PassportEntity> entityList);

}
