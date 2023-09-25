package ru.veselov.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import ru.veselov.authservice.entity.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query("SELECT u FROM UserEntity u join fetch u.roles where u.id= :id AND u.isDeleted=FALSE")
    @NonNull
    Optional<UserEntity> findById(@NonNull @Param("id") UUID id);

    @Query("SELECT u FROM UserEntity  u join fetch u.roles where  u.id= :email AND u.isDeleted=FALSE")
    Optional<UserEntity> findByEmail(@NonNull @Param("email") String email);

    @Query("SELECT u FROM UserEntity u join fetch u.roles where u.isDeleted=FALSE")
    List<UserEntity> findAll();

    @Modifying
    @Query("UPDATE UserEntity u SET u.isDeleted=TRUE where u.id= : id")
    void deleteById(@Param("id") @NonNull UUID id);

    @Modifying
    @Query("DELETE UserEntity u where u.isDeleted=TRUE ")
    void deleteForever();

}
