package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface PassportRepository extends JpaRepository<PassportEntity, UUID> {

    @Query(value = "SELECT p FROM PassportEntity  p where p.printDate BETWEEN :after AND :before",
            countQuery = "SELECT COUNT(p) FROM PassportEntity  p where p.printDate BETWEEN :after AND :before")
    Page<PassportEntity> findAllBetweenDates(@Param("after") LocalDate after,
                                                 @Param("before") LocalDate before, Pageable pageable);

    @Query("SELECT COUNT(p) FROM PassportEntity p where p.printDate BETWEEN :after AND :before")
    long countAllBetweenDates(@Param("after") LocalDate after,
                              @Param("before") LocalDate before);


}
