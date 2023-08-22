package ru.veselov.transducersmanagingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.model.SerialNumber;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SerialNumberMapper {

    SerialNumber toModel(SerialNumberEntity serialNumberEntity);

    List<SerialNumber> toModelList(List<SerialNumberEntity> entityList);

}
