package ru.veselov.taskservice.validation;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
import ru.veselov.taskservice.controller.TaskLaunchController;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.dto.SerialNumberDto;
import ru.veselov.taskservice.service.TaskLaunchService;

import java.util.Collections;
import java.util.List;

@WebMvcTest(TaskLaunchController.class)
class TaskLaunchControllerValidationTest {

    @MockBean
    TaskLaunchService taskLaunchService;

    @Autowired
    MockMvc mockMvc;

    @ParameterizedTest
    @SneakyThrows
    @ArgumentsSource(WrongAndNullUUIDArgumentProvider.class)
    void shouldReturnValidationErrorForDtoForWrongOrNullTemplateId(String templateId) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setTemplateId(templateId);
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        //TODO add field checking after adding ExceptionHandler
    }

    @Test
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForPrintDate() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setPrintDate(null);
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        //TODO add field checking after adding ExceptionHandler
    }

    @Test
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForEmptySerials() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(Collections.emptyList());
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        //TODO add field checking after adding ExceptionHandler
    }

    @ParameterizedTest
    @SneakyThrows
    @ArgumentsSource(WrongAndNullUUIDArgumentProvider.class)
    void shouldReturnValidationErrorForDtoForSerialWrongOrNullUid(String serialId) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(new SerialNumberDto(serialId, "1234")));
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        //TODO add field checking after adding ExceptionHandler
    }

    @ParameterizedTest
    @NullAndEmptySource
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForSerialWrongNumber(String serial) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(new SerialNumberDto(TestUtils.SERIAL_ID.toString(), serial)));
        String contentString = TestUtils.jsonStringFromGeneratePassportsDto(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
        //TODO add field checking after adding ExceptionHandler
    }

}
