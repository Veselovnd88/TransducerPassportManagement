package ru.veselov.generatebytemplate.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.generatebytemplate.entity.ResultFileEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResultFileRepository extends JpaRepository<ResultFileEntity, UUID> {

    @NotNull
    @Query("SELECT f FROM ResultFileEntity f join fetch f.templateEntity where f.id= :id")
    Optional<ResultFileEntity> findById(@NotNull @Param("id") UUID id);

    @Modifying
    @Query("DELETE ResultFileEntity f where f.synced=false AND f.createdAt < :deleteDate")
    void deleteAllWithUnSyncFalse(LocalDateTime deleteDate);

    @Modifying
    @Query("DELETE ResultFileEntity f where f.createdAt < :deleteDate")
    void deleteExpiredResultFiles(LocalDateTime deleteDate);

}
