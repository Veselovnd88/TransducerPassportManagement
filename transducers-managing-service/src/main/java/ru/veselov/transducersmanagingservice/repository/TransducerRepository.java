package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface TransducerRepository extends JpaRepository<TransducerEntity, UUID> {

    @Query("SELECT t FROM TransducerEntity t where t.art= :ptArt")
    Optional<TransducerEntity> findByArt(@Param("ptArt") String ptArt);

}
