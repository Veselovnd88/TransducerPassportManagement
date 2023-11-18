package ru.veselov.taskservice.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.EventType;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.utils.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
class TaskStatusSseControllerTest extends PostgresContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TaskRepository taskRepository;

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldReturnFluxWithSSEStatusesFoStartedTask() {
        String taskId = saveTaskToRepo(TaskStatus.STARTED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri("/api/v1/task/status-stream/" + taskId)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });

        Flux<ServerSentEvent<Task>> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.INIT.toString());
                })
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.CONNECTED.toString());
                })
                .thenCancel().verify();
    }

    @Test
    void shouldSendErrorMessageIfTaskNotFound() {
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri("/api/v1/task/status-stream/" + TestUtils.TASK_ID_STR)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });

        Flux<ServerSentEvent<Task>> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.INIT.toString());
                })
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.ERROR.toString());
                })
                .thenCancel().verify();
    }

    @Test
    void shouldSendMessageAndCompleteSubscription() {
        String taskId = saveTaskToRepo(TaskStatus.PERFORMED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri("/api/v1/task/status-stream/" + taskId)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });

        Flux<ServerSentEvent<Task>> responseBody = fluxResult.getResponseBody();
        StepVerifier.create(responseBody)
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.INIT.toString());
                })
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.CONNECTED.toString());
                })
                .expectComplete()
                .verify();
    }

    private String saveTaskToRepo(TaskStatus status) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setStatus(status);
        taskEntity.setTemplateId(TestUtils.TEMPLATE_ID);
        taskEntity.setPrintDate(TestUtils.PRINT_DATE);
        taskEntity.setUsername(TestUtils.USERNAME);
        TaskEntity saved = taskRepository.save(taskEntity);
        return saved.getTaskId().toString();
    }

}
