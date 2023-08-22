package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;

import java.util.List;
import java.util.UUID;

public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, UUID> {

    @Query("SELECT s FROM SerialNumberEntity s where s.number= :number")
    List<SerialNumberEntity> findAllByNumber(@Param("number") String number);

    @Query(value = "SELECT s FROM SerialNumberEntity s where s.ptArt= :ptARt",
            countQuery = "SELECT COUNT(s) FROM  SerialNumberEntity  s where s.ptArt= :ptArt")
    Page<SerialNumberEntity> findAllByPtArt(@Param("ptArt") String ptArt, Pageable pageable);

    @Query("SELECT COUNT(s) FROM SerialNumberEntity s where s.ptArt= :ptArt")
    long countAllByPtArt(@Param("ptArt") String ptArt);

    long countAllByPtArtBetweenDates();


}
