package ru.veselov.taskservice.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.taskservice.TestURLsConstants;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.entity.SerialNumberEntity;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.utils.AppConstants;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
@ActiveProfiles("test")
public class TaskControllerIntegrationTest extends PostgresContainersConfig {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    TaskRepository taskRepository;

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
        serialNumberRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldGetTaskById() {
        TaskEntity taskEntity = saveTaskWithoutSerialsToRepo(false);
        taskEntity.setSerials(Set.of(new SerialNumberEntity(TestUtils.SERIAL_ID, "123")));
        mockMvc.perform(MockMvcRequestBuilders.get(TestURLsConstants.TASK + "/" + taskEntity.getTaskId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").value(taskEntity.getTaskId().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.started").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.performed").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.printDate")
                        .value(taskEntity.getPrintDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.performedAt").doesNotExist());
    }

    @Test
    @SneakyThrows
    void shouldGetPerformedTasksForUsername() {
        TaskEntity performedTask = saveTaskWithoutSerialsToRepo(true);
        saveTaskWithoutSerialsToRepo(false);//not performed task
        mockMvc.perform(MockMvcRequestBuilders.get(TestURLsConstants.TASK + TestURLsConstants.PERFORMED)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].taskId").value(performedTask.getTaskId().toString()));
    }

    @Test
    @SneakyThrows
    void shouldGetNotPerformedTasksForUsername() {
        saveTaskWithoutSerialsToRepo(true);//performed task
        TaskEntity notPerformedTask = saveTaskWithoutSerialsToRepo(false);
        mockMvc.perform(MockMvcRequestBuilders.get(TestURLsConstants.TASK + TestURLsConstants.CURRENT)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.[0].taskId")
                        .value(notPerformedTask.getTaskId().toString()));
    }

    private TaskEntity saveTaskWithoutSerialsToRepo(boolean performed) {
        TaskEntity taskEntity = TaskEntity.builder()
                .printDate(TestUtils.PRINT_DATE)
                .username(TestUtils.USERNAME)
                .templateId(TestUtils.TEMPLATE_ID)
                .performed(performed)
                .started(true)
                .build();
        return taskRepository.save(taskEntity);
    }

}
