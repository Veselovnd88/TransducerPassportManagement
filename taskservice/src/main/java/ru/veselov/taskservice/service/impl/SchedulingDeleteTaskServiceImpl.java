package ru.veselov.taskservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.service.SchedulingDeleteTaskService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingDeleteTaskServiceImpl implements SchedulingDeleteTaskService {

    @Value("${scheduler.not-started-task-lifetime}")
    private int notStartedTaskLifeTime;

    private final TaskRepository taskRepository;

    @Transactional
    @Scheduled(cron = "${scheduler.delete-not-started-tasks}")
    @Override
    public void deleteNotStarted() {
        LocalDateTime deleteDate = LocalDateTime.now().minusDays(notStartedTaskLifeTime);
        taskRepository.deleteNotStartedTasks(deleteDate);
        log.info("Not started tasks [older than {}] deleted", notStartedTaskLifeTime);
    }

}
