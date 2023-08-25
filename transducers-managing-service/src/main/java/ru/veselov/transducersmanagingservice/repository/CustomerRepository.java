package ru.veselov.transducersmanagingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    @Query("SELECT c from CustomerEntity c where c.inn= :inn")
    Optional<CustomerEntity> findByInn(@Param("inn") String inn);

}
