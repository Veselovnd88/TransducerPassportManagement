package ru.veselov.passportprocessing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.passportprocessing.entity.PassportEntity;

import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<PassportEntity, UUID> {
}
