package ru.veselov.taskservice.it;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import lombok.SneakyThrows;
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
import ru.veselov.taskservice.utils.TestURLsConstants;
import ru.veselov.taskservice.utils.TestUtils;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.it.config.RestTemplateTestConfiguration;
import ru.veselov.taskservice.repository.SerialNumberRepository;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.utils.AppConstants;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 30001)
@Import(RestTemplateTestConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
public class TaskLaunchControllerIntegrationTest extends PostgresContainersConfig {

    private static final Integer GEN_SERVICE_PORT = 30001;

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
    void shouldLaunchTaskAndReturnTaskModelWithData() {
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/.*"))
                .willReturn(WireMock.aResponse().withStatus(202)));
        GeneratePassportsDto generatePassportsDto = TestUtils.getGeneratePassportsDto();
        String contentString = TestUtils.jsonStringFromObject(generatePassportsDto);
        mockMvc.perform(MockMvcRequestBuilders
                        .post(TestURLsConstants.TASK_LAUNCH)
                        .content(contentString)
                        .header(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isAccepted())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskId").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(TaskStatus.STARTED.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.printDate")
                        .value(generatePassportsDto.getPrintDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.createdAt").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.performedAt").doesNotExist());
    }

}
