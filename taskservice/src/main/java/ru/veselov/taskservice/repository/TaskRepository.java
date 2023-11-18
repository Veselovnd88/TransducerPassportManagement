package ru.veselov.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.taskservice.entity.TaskEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    String USERNAME = "username";

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH  t.serials " +
            "WHERE  t.status= 'PERFORMED' AND t.username= :username" +
            "  ORDER BY t.performedAt LIMIT 20")
    List<TaskEntity> findAllPerformedTasksByUsername(@Param(USERNAME) String username);

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH  t.serials " +
            "WHERE  t.status!='PERFORMED' AND t.username= :username" +
            " ORDER BY t.createdAt LIMIT 20")
    List<TaskEntity> findAllNotPerformedTasksByUsername(@Param(USERNAME) String username);

    @Modifying
    @Query("DELETE TaskEntity t WHERE t.status='CREATED' AND t.createdAt < :deleteDate")
    void deleteNotStartedTasks(LocalDateTime deleteDate);

}
