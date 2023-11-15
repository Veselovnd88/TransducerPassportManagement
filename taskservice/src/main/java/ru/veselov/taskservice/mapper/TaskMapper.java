package ru.veselov.taskservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.model.Task;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    Task toModel(TaskEntity taskEntity);

    List<Task> toModels(List<TaskEntity> entities);

}
