package ru.veselov.taskservice.controller;

import lombok.SneakyThrows;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.veselov.taskservice.TestURLsConstants;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskService;
import ru.veselov.taskservice.utils.AppConstants;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    @SneakyThrows
    void shouldGetTask() {
        Task task = Instancio.create(Task.class);
        Mockito.when(taskService.getTask(TestUtils.TASK_ID_STR)).thenReturn(task);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + "/" + TestUtils.TASK_ID)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(taskService).getTask(TestUtils.TASK_ID_STR);
    }

    @Test
    @SneakyThrows
    void shouldGetPerformedTasks() {
        Task task = Instancio.create(Task.class);
        Mockito.when(taskService.getPerformedTasks(TestUtils.USERNAME)).thenReturn(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + TestURLsConstants.PERFORMED)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(taskService).getPerformedTasks(TestUtils.USERNAME);
    }

    @Test
    @SneakyThrows
    void shouldGetCurrentTasks() {
        Task task = Instancio.create(Task.class);
        Mockito.when(taskService.getNotPerformedTasks(TestUtils.USERNAME)).thenReturn(List.of(task));

        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + TestURLsConstants.CURRENT)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(taskService).getNotPerformedTasks(TestUtils.USERNAME);
    }

}