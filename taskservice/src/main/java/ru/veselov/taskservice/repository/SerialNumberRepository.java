package ru.veselov.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.taskservice.entity.SerialNumberEntity;

import java.util.UUID;

public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, UUID> {
}
