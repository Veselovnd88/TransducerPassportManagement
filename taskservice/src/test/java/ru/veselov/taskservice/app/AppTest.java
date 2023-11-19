package ru.veselov.taskservice.app;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import ru.veselov.taskservice.it.config.KafkaProducerTestConfiguration;
import ru.veselov.taskservice.it.config.RestTemplateTestConfiguration;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureWebTestClient
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Import({KafkaProducerTestConfiguration.class, RestTemplateTestConfiguration.class})
@WireMockTest(httpPort = 30007)
@DirtiesContext
@ActiveProfiles("test")
public class AppTest extends PostgresContainersConfig {

    private static final String WIREMOCK_URL = "/" + TestUtils.TASK_ID_STR;

    private static final Integer GEN_SERVICE_PORT = 30007;

    private final static String GENERATE_SERVICE_URL = "http://localhost:%d".formatted(GEN_SERVICE_PORT);

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldStartTaskAndReceiveAllNotificationsBySse() {

    }
}
