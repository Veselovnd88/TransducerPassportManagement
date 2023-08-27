package ru.veselov.transducersmanagingservice.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class SerialControllerIntegrationTest  extends PostgresContainersConfig {

    private final static String URL_PREFIX = "/api/v1/serials";

    @Autowired
    WebTestClient webTestClient;


}
