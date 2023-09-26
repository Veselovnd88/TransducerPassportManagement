package ru.veselov.authservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.veselov.authservice.dto.SignUpDto;
import ru.veselov.authservice.entity.UserEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserEntity signUpDtoToEntity(SignUpDto signUpDto);
}
