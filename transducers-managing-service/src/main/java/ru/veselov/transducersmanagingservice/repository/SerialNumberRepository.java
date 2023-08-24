package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SerialNumberRepository extends JpaRepository<SerialNumberEntity, UUID> {

    @Query("SELECT s FROM SerialNumberEntity s where s.number= :number")
    List<SerialNumberEntity> findAllByNumber(@Param("number") String number);

    @Query(value = "SELECT s FROM SerialNumberEntity s where s.ptArt= :ptArt",
            countQuery = "SELECT COUNT(s) FROM  SerialNumberEntity  s where s.ptArt= :ptArt")
    Page<SerialNumberEntity> findAllByPtArt(@Param("ptArt") String ptArt, Pageable pageable);

    @Query("SELECT COUNT(s) FROM SerialNumberEntity s where s.ptArt= :ptArt")
    long countAllByPtArt(@Param("ptArt") String ptArt);

    @Query(value = "SELECT s FROM SerialNumberEntity  s where s.ptArt= :ptArt " +
            "AND s.savedAt BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(s) FROM SerialNumberEntity  s where s.ptArt= :ptArt " +
                    "AND s.savedAt BETWEEN :after AND :before")
    Page<SerialNumberEntity> findAllByPtARtBetweenDates(@Param("ptArt") String ptArt,
                                                        @Param("after") LocalDate after,
                                                        @Param("before") LocalDate before, Pageable pageable);

    @Query("SELECT COUNT(s) FROM SerialNumberEntity  s where s.ptArt= :ptArt " +
            "AND s.savedAt BETWEEN :after AND :before")
    long countAllByPtArtBetweenDates(@Param("ptArt") String ptArt, @Param("after") LocalDate after,
                                     @Param("before") LocalDate before);

    @Query(value = "SELECT s FROM SerialNumberEntity  s where s.savedAt BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(s) FROM SerialNumberEntity  s where s.savedAt BETWEEN :after AND :before")
    Page<SerialNumberEntity> findAllBetweenDates(@Param("after") LocalDate after,
                                                 @Param("before") LocalDate before, Pageable pageable);

    @Query("SELECT COUNT(s) FROM SerialNumberEntity  s where s.savedAt BETWEEN :after AND :before")
    long countAllBetweenDates(@Param("after") LocalDate after,
                              @Param("before") LocalDate before);
}
