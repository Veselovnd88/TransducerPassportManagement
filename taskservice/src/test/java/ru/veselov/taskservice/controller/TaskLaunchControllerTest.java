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
import ru.veselov.taskservice.util.TestURLsConstants;
import ru.veselov.taskservice.util.TestUtils;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.service.TaskLaunchService;
import ru.veselov.taskservice.util.AppConstants;

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
        Task task = TestUtils.getTask();
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        Mockito.when(taskLaunchService.launchTask(generatePassportsDto, TestUtils.USERNAME)).thenReturn(task);
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        Mockito.verify(taskLaunchService).launchTask(generatePassportsDto, TestUtils.USERNAME);
    }

}
