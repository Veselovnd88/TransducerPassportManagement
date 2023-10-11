package ru.veselov.generatebytemplate.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.generatebytemplate.entity.GeneratedResultFileEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GeneratedResultFileRepository extends JpaRepository<GeneratedResultFileEntity, UUID> {

    @NotNull
    @Query("SELECT f FROM GeneratedResultFileEntity f join fetch f.templateEntity where f.id= :id")
    Optional<GeneratedResultFileEntity> findById(@NotNull @Param("id") UUID id);

    @Modifying
    @Query("DELETE GeneratedResultFileEntity f where f.synced=false AND f.createdAt < :deleteDate")
    void deleteAllWithUnSyncFalse(LocalDateTime deleteDate);

    @Modifying
    @Query("DELETE GeneratedResultFileEntity f where f.createdAt < :deleteDate")
    void deleteExpiredResultFiles(LocalDateTime deleteDate);

}
