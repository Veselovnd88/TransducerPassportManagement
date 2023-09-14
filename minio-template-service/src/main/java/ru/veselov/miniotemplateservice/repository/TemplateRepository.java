package ru.veselov.miniotemplateservice.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.miniotemplateservice.entity.TemplateEntity;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface TemplateRepository extends JpaRepository<TemplateEntity, UUID> {

    @Query("SELECT t FROM TemplateEntity t where t.templateName= :templateName")
    Optional<TemplateEntity> findByName(@Param("templateName") String templateName);

    @NotNull
    @Query("SELECT t FROM TemplateEntity t where t.id= :templateId")
    Optional<TemplateEntity> findById(@NotNull @Param("templateId") UUID templateId);

    @NotNull
    @Query(value = "SELECT t FROM TemplateEntity t where t.synced=true ",
            countQuery = "SELECT COUNT(t) FROM TemplateEntity t")
    Page<TemplateEntity> findAll(@NotNull Pageable pageable);

    @Query("SELECT COUNT(t) FROM TemplateEntity t where t.synced=true ")
    long countAll();

    @Query(value = "SELECT t FROM TemplateEntity t where t.ptArt LIKE %:ptArt% AND  t.synced=true",
            countQuery = "SELECT COUNT(t) FROM TemplateEntity t where t.ptArt LIKE %:ptArt% AND t.synced=true")
        //or CONCAT('%', :ptArt, '%')
    Page<TemplateEntity> findAllByPtArt(@Param("ptArt") String ptArt, Pageable pageable);

    @Query("SELECT COUNT(t) FROM TemplateEntity t where t.ptArt LIKE %:ptArt% AND t.synced=true")
    long countAllByPtArt(@Param("ptArt") String ptArt);

}
