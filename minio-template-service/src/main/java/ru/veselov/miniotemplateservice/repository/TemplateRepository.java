package ru.veselov.miniotemplateservice.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;

import java.util.Optional;
import java.util.UUID;

public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {

    @Query("SELECT t FROM TemplateEntity t where t.templateName= :templateName")
    Optional<TemplateEntity> findByName(@Param("templateName") String templateName);

    @NotNull
    @Query("SELECT t FROM TemplateEntity t where t.id= :templateId")
    Optional<TemplateEntity> findById(@NotNull @Param("templateId") UUID templateId);

}
