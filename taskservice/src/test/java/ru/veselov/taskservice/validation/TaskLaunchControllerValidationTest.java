package ru.veselov.taskservice.validation;

import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.taskservice.utils.TestURLsConstants;
import ru.veselov.taskservice.utils.TestUtils;
import ru.veselov.taskservice.utils.argumentproviders.WrongAndNullUUIDArgumentProvider;
import ru.veselov.taskservice.controller.TaskLaunchController;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.dto.SerialNumberDto;
import ru.veselov.taskservice.exception.error.ErrorCode;
import ru.veselov.taskservice.service.TaskLaunchService;
import ru.veselov.taskservice.utils.AppConstants;

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
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value("templateId"));
    }

    @Test
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForPrintDate() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setPrintDate(null);
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value("printDate"));
    }

    @Test
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForEmptySerials() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(Collections.emptyList());
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value("serials"));
    }

    @ParameterizedTest
    @SneakyThrows
    @ArgumentsSource(WrongAndNullUUIDArgumentProvider.class)
    void shouldReturnValidationErrorForDtoForSerialWrongOrNullUid(String serialId) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(new SerialNumberDto(serialId, "1234")));
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value(
                                Matchers.allOf(Matchers.startsWith("serials"), Matchers.endsWith("serialId"))));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @SneakyThrows
    void shouldReturnValidationErrorForDtoForSerialWrongNumber(String serial) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(new SerialNumberDto(TestUtils.SERIAL_ID.toString(), serial)));
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(TestUtils.USERNAME, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError()).andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value(
                                Matchers.allOf(Matchers.startsWith("serials"), Matchers.endsWith("serial"))));
    }

    @ParameterizedTest
    @EmptySource
    @SneakyThrows
    void shouldReturnValidationErrorForEmptyUsernameHeader(String username) {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD).value(TestUtils.USERNAME));
    }

    @Test
    @SneakyThrows
    void shouldReturnValidationErrorForNullUsernameHeader() {
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_ERROR_CODE).value(ErrorCode.ERROR_VALIDATION.toString()))
                .andExpect(MockMvcResultMatchers
                        .jsonPath(TestUtils.JSON_VIOLATIONS_FIELD)
                        .value(Matchers.endsWith(AppConstants.SERVICE_USERNAME_HEADER)));
    }

}
