package ru.veselov.taskservice.it;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import ru.veselov.taskservice.dto.TaskResultDto;
import ru.veselov.taskservice.entity.TaskEntity;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.event.EventType;
import ru.veselov.taskservice.it.config.KafkaProducerTestConfiguration;
import ru.veselov.taskservice.model.Task;
import ru.veselov.taskservice.repository.TaskRepository;
import ru.veselov.taskservice.testcontainers.PostgresContainersConfig;
import ru.veselov.taskservice.util.TestURLsConstants;
import ru.veselov.taskservice.util.TestUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Import({KafkaProducerTestConfiguration.class})
@DirtiesContext
@ActiveProfiles("test")
class TaskStatusSseControllerTest extends PostgresContainersConfig {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    KafkaTemplate<String, TaskResultDto> kafkaTemplate;

    @AfterEach
    void clear() {
        taskRepository.deleteAll();
    }

    @Test
    void shouldReturnFluxWithSSEStatusesFoStartedTask() {
        String taskId = saveTaskToRepo(TaskStatus.STARTED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri(TestURLsConstants.TASK_STATUS_STREAM + taskId)
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
                .uri(TestURLsConstants.TASK_STATUS_STREAM + TestUtils.TASK_ID_STR)
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
                .uri(TestURLsConstants.TASK_STATUS_STREAM + taskId)
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

    @Test
    void shouldSendMessageAfterSuccessUpdateFromKafka() {
        String taskId = saveTaskToRepo(TaskStatus.STARTED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri(TestURLsConstants.TASK_STATUS_STREAM + taskId)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });
        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID_STR, null, TaskStatus.PERFORMED);
        kafkaTemplate.send("task", taskId, taskResultDto);
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
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.UPDATED.toString());
                })
                .thenCancel().verify();
    }

    @Test
    void shouldSendMessageAfterErrorUpdateFromKafka() {
        String taskId = saveTaskToRepo(TaskStatus.STARTED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri(TestURLsConstants.TASK_STATUS_STREAM + taskId)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });
        TaskResultDto taskResultDto = new TaskResultDto(null, "error", TaskStatus.FAILED);
        kafkaTemplate.send("task", taskId, taskResultDto);
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
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.UPDATED.toString());
                })
                .thenCancel().verify();
    }

    @Test
    void shouldSendErrorAfterUpdateFromKafka() {
        String taskId = saveTaskToRepo(TaskStatus.STARTED);
        FluxExchangeResult<ServerSentEvent<Task>> fluxResult = webTestClient.get()
                .uri(TestURLsConstants.TASK_STATUS_STREAM + taskId)
                .exchange().expectStatus().is2xxSuccessful()
                .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE)
                .returnResult(new ParameterizedTypeReference<>() {
                });
        TaskResultDto taskResultDto = new TaskResultDto(null, "error", TaskStatus.FAILED);
        taskRepository.deleteAll();
        kafkaTemplate.send("task", taskId, taskResultDto);
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
                .expectNextMatches(x -> {
                    assert x.event() != null;
                    return x.event().equals(EventType.ERROR.toString());
                })
                .thenCancel().verify();
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
