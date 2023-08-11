package ru.veselov.miniotemplateservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;

import java.util.UUID;

public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {
}
