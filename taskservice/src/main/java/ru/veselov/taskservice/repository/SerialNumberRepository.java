package ru.veselov.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.taskservice.entity.SerialNumberEntity;

import java.util.Optional;
import java.util.UUID;

public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, UUID> {

    @Query("SELECT s FROM SerialNumberEntity s WHERE s.serialId= :serial_id")
    Optional<SerialNumberEntity> findSerialNumberById(@Param("serial_id") UUID serialId);

}
