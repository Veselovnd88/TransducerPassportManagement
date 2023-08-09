package ru.veselov.passportprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.passportprocessing.entity.PassportEntity;

import java.util.UUID;

public interface PassportRepository extends JpaRepository<PassportEntity, UUID> {
}
