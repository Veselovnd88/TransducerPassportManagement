package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;

import java.util.UUID;

public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, UUID> {
}
