package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<PassportEntity, UUID> {

    @Query(value = "SELECT p FROM PassportEntity  p" +
            " left join fetch p.template" +
            " left join fetch  p.serialNumber " +
            "where p.printDate BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(p) FROM PassportEntity  p where p.printDate BETWEEN :after AND :before")
    Page<PassportEntity> findAllBetweenDates(@Param("after") LocalDate after,
                                             @Param("before") LocalDate before, Pageable pageable);

    @Query("SELECT COUNT(p) FROM PassportEntity p where p.printDate BETWEEN :after AND :before")
    long countAllBetweenDates(@Param("after") LocalDate after,
                              @Param("before") LocalDate before);

    @Query(value = "SELECT p FROM PassportEntity  p" +
            " left join fetch p.template" +
            " left join fetch  p.serialNumber " +
            "where p.serialNumber.number= :serialNumber" +
            " AND p.printDate BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(p) FROM PassportEntity  p " +
                    "where p.serialNumber.number= :serialNumber " +
                    "AND p.printDate BETWEEN :after AND :before")
    Page<PassportEntity> findAllBySerialBetweenDates(@Param("serialNumber") String serialNumber,
                                                     @Param("after") LocalDate after,
                                                     @Param("before") LocalDate before,
                                                     Pageable pageable);

    @Query("SELECT COUNT(p) FROM PassportEntity p where p.serialNumber.number= :serialNumber" +
            " AND p.printDate BETWEEN :after AND :before")
    long countBySerialAllBetweenDates(@Param("serialNumber") String serialNumber,
                                      @Param("after") LocalDate after,
                                      @Param("before") LocalDate before);

    @Query(value = "SELECT p FROM PassportEntity  p" +
            " left join fetch p.template" +
            " left join fetch  p.serialNumber " +
            "where p.serialNumber.ptArt= :ptArt" +
            " AND p.printDate BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(p) FROM PassportEntity  p " +
                    "where p.serialNumber.ptArt= :ptArt " +
                    "AND p.printDate BETWEEN :after AND :before")
    Page<PassportEntity> findAllByPtArtBetweenDates(@Param("ptArt") String ptArt,
                                                    @Param("after") LocalDate after,
                                                    @Param("before") LocalDate before,
                                                    Pageable pageable);

    @Query("SELECT COUNT(p) FROM PassportEntity p where p.serialNumber.ptArt= :ptArt" +
            " AND p.printDate BETWEEN :after AND :before")
    long countByPtArtAllBetweenDates(@Param("ptArt") String ptArt,
                                     @Param("after") LocalDate after,
                                     @Param("before") LocalDate before);

    @Modifying
    @Query("DELETE FROM PassportEntity p where p.serialNumber=NULL AND p.template=NULL")
    void deleteWithNullSerialAndTemplate();

}
