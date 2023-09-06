package ru.veselov.transducersmanagingservice.app;

import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
class TransducerControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/transducer";

    @Autowired
    TransducerRepository transducerRepository;

    @Autowired
    WebTestClient webTestClient;

    @AfterEach
    void clear() {
        transducerRepository.deleteAll();
    }

    @Test
    void shouldSave() {
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isAccepted().expectBody()
                .jsonPath("$.art").isEqualTo(transducerDto.getArt());

        Optional<TransducerEntity> foundTransducer = transducerRepository.findByArt(transducerDto.getArt());
        Assertions.assertThat(foundTransducer).isPresent();
    }

    @Test
    void shouldReturnErrorIfTransducerWithArtExists() {
        saveTransducerToRepo();
        TransducerDto transducerDto = Instancio.of(TransducerDto.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_CONFICT.toString());
    }

    @Test
    void shouldReturnTransducerById() {
        TransducerEntity transducerEntity = saveTransducerToRepo();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/id/" + transducerEntity.getId()).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(transducerEntity.getId().toString())
                .jsonPath("$.art").isEqualTo(transducerEntity.getArt())
                .jsonPath("$.options").isEqualTo(transducerEntity.getOptions());
    }

    @Test
    void shouldReturnTransducerByArt() {
        TransducerEntity transducerEntity = saveTransducerToRepo();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/art/" + transducerEntity.getArt()).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.art").isEqualTo(transducerEntity.getArt())
                .jsonPath("$.options").isEqualTo(transducerEntity.getOptions());
    }

    @Test
    void shouldGetAll() {
        TransducerEntity transducerEntity = saveTransducerToRepo();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].art").isEqualTo(transducerEntity.getArt());
    }

    @Test
    void shouldDelete() {
        TransducerEntity transducerEntity = saveTransducerToRepo();

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete/" + transducerEntity.getId()).build())
                .exchange().expectStatus().isAccepted();

        Optional<TransducerEntity> foundTransducer = transducerRepository.findById(transducerEntity.getId());
        Assertions.assertThat(foundTransducer).isNotPresent();
    }

    @Test
    void shouldUpdate() {
        TransducerEntity transducerEntity = saveTransducerToRepo();
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/update/" + transducerEntity.getId()).build())
                .bodyValue(transducerDto).exchange().expectStatus().isAccepted();

        Optional<TransducerEntity> foundTransducer = transducerRepository.findById(transducerEntity.getId());
        Assertions.assertThat(foundTransducer).isPresent();
        TransducerEntity updatedEntity = foundTransducer.get();
        Assertions.assertThat(updatedEntity.getArt()).isEqualTo(transducerDto.getArt());
    }


    private TransducerEntity saveTransducerToRepo() {
        TransducerEntity transducerEntity = Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();
        return transducerRepository.save(transducerEntity);
    }

}
