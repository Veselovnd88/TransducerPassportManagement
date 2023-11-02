package ru.veselov.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.taskservice.entity.TaskEntity;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
}
