package ru.veselov.taskservice.controller;

import lombok.SneakyThrows;
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
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskLaunchService;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class TaskLaunchControllerTest {

    @Mock
    TaskLaunchService taskLaunchService;

    @InjectMocks
    TaskLaunchController taskLaunchController;

    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskLaunchController).build();
    }

    @Test
    @SneakyThrows
    void shouldLaunchTask() {
        Task task = new Task(TestUtils.TASK_ID, false, LocalDateTime.now(), LocalDateTime.now());
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Mockito.when(taskLaunchService.startTask(generatePassportsDto, TestUtils.USERNAME)).thenReturn(task);
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(taskLaunchService).startTask(generatePassportsDto, TestUtils.USERNAME);
    }

}
