package ru.veselov.taskservice.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.veselov.taskservice.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
@SuppressWarnings("rawtypes")
class TaskStatusSseControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnFluxWithSSEStatuses() {
        FluxExchangeResult<ServerSentEvent> fluxResult = webTestClient.get()
                .uri("/api/v1/task/status-stream/" + TestUtils.TASK_ID_STR)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(ServerSentEvent.class);

        Flux<ServerSentEvent> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextMatches(x -> {
                    assert x.data() != null;
                    return ((String) x.data()).startsWith("Connected");
                })
                .thenCancel().verify();
    }

}
