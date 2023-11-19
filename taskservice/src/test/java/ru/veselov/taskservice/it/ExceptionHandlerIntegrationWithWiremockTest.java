package ru.veselov.taskservice.it;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.veselov.taskservice.util.TestURLsConstants;
import ru.veselov.taskservice.util.TestUtils;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.exception.error.ErrorCode;
import ru.veselov.taskservice.exception.error.ValidationErrorResponse;
import ru.veselov.taskservice.exception.error.ViolationError;
import ru.veselov.taskservice.it.config.RestTemplateTestConfiguration;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.util.AppConstants;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 30002)
@Import(RestTemplateTestConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
public class ExceptionHandlerIntegrationWithWiremockTest extends PostgresContainersConfig {

    private static final Integer GEN_SERVICE_PORT = 30002;

    private final static String GENERATE_SERVICE_URL = "http://localhost:%d".formatted(GEN_SERVICE_PORT);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    TaskRepository taskRepository;

    @BeforeEach
    void init() {
        WireMock.configureFor("localhost", GEN_SERVICE_PORT);
    }

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
        serialNumberRepository.deleteAll();
    }

    @DynamicPropertySource
    static void setUpUrls(DynamicPropertyRegistry registry) {
        registry.add("generate-service.url", () -> GENERATE_SERVICE_URL);
    }

    @Test
    @SneakyThrows
    void shouldReturnGenerateServiceErrorForGenerateServiceValidationError() {
        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse("1", List.of(
                new ViolationError("field", "message", "null")
        ));
        byte[] responseBytes = TestUtils.jsonStringFromObject(validationErrorResponse).getBytes();
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/.*"))
                .willReturn(WireMock.aResponse().withStatus(400).withBody(responseBytes)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(TestUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.SERVICE_ERROR.toString()));

        List<TaskEntity> savedTasks = taskRepository.findAll();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(savedTasks).isNotEmpty().hasSize(1),
                () -> Assertions.assertThat(savedTasks.get(0)).isNotNull(),
                () -> Assertions.assertThat(savedTasks.get(0).getStatus()).isEqualTo(TaskStatus.CREATED)
        );
    }

    @Test
    @SneakyThrows
    void shouldReturnGenerateServiceErrorForGenerateServiceInternalError() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/.*"))
                .willReturn(WireMock.aResponse().withStatus(500)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);

        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is5xxServerError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath(TestUtils.JSON_ERROR_CODE)
                        .value(ErrorCode.SERVICE_ERROR.toString()));

        List<TaskEntity> savedTasks = taskRepository.findAll();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(savedTasks).isNotEmpty().hasSize(1),
                () -> Assertions.assertThat(savedTasks.get(0)).isNotNull(),
                () -> Assertions.assertThat(savedTasks.get(0).getStatus()).isEqualTo(TaskStatus.CREATED)
        );
    }

}
