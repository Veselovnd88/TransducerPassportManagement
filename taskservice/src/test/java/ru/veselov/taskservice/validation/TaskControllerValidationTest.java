package ru.veselov.taskservice.validation;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.taskservice.TestURLsConstants;
import ru.veselov.taskservice.TestUtils;
import ru.veselov.taskservice.WrongAndNullUUIDArgumentProvider;
import ru.veselov.taskservice.controller.TaskController;
import ru.veselov.taskservice.exception.error.ErrorCode;
import ru.veselov.taskservice.service.TaskService;
import ru.veselov.taskservice.utils.AppConstants;

@WebMvcTest(TaskController.class)
public class TaskControllerValidationTest {

    @MockBean
    TaskService taskService;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @ArgumentsSource(WrongAndNullUUIDArgumentProvider.class)
    @SneakyThrows
    void shouldReturnValidationErrorForWrongTaskId(String taskId) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + "/" + taskId)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value("taskId"));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void shouldReturnValidationErrorForEmptyUsernameForPerformed(String username) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + "/performed")
                        .header(AppConstants.SERVICE_USERNAME_HEADER, username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value(TestUtils.USERNAME));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void shouldReturnValidationErrorForEmptyUsernameForCurrent(String username) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + "/current")
                        .header(AppConstants.SERVICE_USERNAME_HEADER, username)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value(TestUtils.USERNAME));
    }

    @ParameterizedTest
    @ValueSource(strings = {TestURLsConstants.PERFORMED, TestURLsConstants.CURRENT})
    @SneakyThrows
    void shouldReturnValidationErrorForNoUsernameHeaderFor(String postfix) {
        mockMvc.perform(MockMvcRequestBuilders
                        .get(TestURLsConstants.TASK + postfix)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD)
                        .value(Matchers.endsWith(AppConstants.SERVICE_USERNAME_HEADER)));
    }

}
