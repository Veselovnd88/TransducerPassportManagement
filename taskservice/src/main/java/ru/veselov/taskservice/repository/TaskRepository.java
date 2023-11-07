package ru.veselov.taskservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.veselov.taskservice.entity.TaskEntity;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH  t.serials where  t.isPerformed=true AND t.username= :username" +
            "  ORDER BY t.performedAt LIMIT 20")
    List<TaskEntity> findAllByUsername(@Param("username") String username);

    @Query("SELECT t FROM TaskEntity t LEFT JOIN FETCH  t.serials where  t.isPerformed=false AND t.username= :username" +
            " ORDER BY t.createdAt LIMIT 20")
    List<TaskEntity> findAllByUsernameCurrent(@Param("username") String username);

}
