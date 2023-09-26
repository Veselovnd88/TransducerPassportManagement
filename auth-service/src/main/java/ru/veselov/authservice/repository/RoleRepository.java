package ru.veselov.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.veselov.authservice.entity.RoleEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {
    @Query("SELECT r FROM RoleEntity r where r.role= :role")
    Optional<RoleEntity> findByRoleName(@Param("role") String role);
}
