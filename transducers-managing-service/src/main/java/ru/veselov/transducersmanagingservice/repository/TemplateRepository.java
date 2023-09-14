package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.TemplateEntity;

import java.util.UUID;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {

}
