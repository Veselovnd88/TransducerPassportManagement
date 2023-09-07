package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransducerRepository extends JpaRepository<TransducerEntity, UUID> {

    @NonNull
    @Query("SELECT t FROM TransducerEntity  t where  t.id= :id")
    Optional<TransducerEntity> findById(@NonNull @Param("id") UUID id);

    @Query("SELECT t FROM TransducerEntity t where t.art= :ptArt")
    Optional<TransducerEntity> findByArt(@Param("ptArt") String ptArt);

    @Query(value = "SELECT t FROM TransducerEntity t",
            countQuery = "SELECT COUNT(t) from TransducerEntity t")
    Page<TransducerEntity> getAll(Pageable pageable);

    @Query("SELECT COUNT(t) FROM TransducerEntity t")
    long countAll();

}
