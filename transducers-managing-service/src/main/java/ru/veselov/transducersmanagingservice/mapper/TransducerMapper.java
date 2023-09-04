package ru.veselov.transducersmanagingservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.model.Transducer;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransducerMapper {


    TransducerEntity toEntity(TransducerDto transducerDto);

    Transducer toModel(TransducerEntity transducerEntity);

    List<Transducer> toModels(List<TransducerEntity> transducerEntityList);

}
