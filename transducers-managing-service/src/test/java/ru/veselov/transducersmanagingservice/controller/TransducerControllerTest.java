package ru.veselov.transducersmanagingservice.controller;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.TransducerDto;
import ru.veselov.transducersmanagingservice.model.Transducer;
import ru.veselov.transducersmanagingservice.service.TransducerService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TransducerControllerTest {

    public static final String URL_PREFIX = "/api/v1/transducer";

    @Mock
    TransducerService transducerService;

    @InjectMocks
    TransducerController transducerController;

    WebTestClient webTestClient;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(transducerController).build();
    }

    @Test
    void shouldSave() {
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(transducerDto)
                .exchange().expectStatus().isAccepted();

        Mockito.verify(transducerService, Mockito.times(1)).saveTransducer(transducerDto);
    }

    @Test
    void shouldFindById() {
        Transducer transducer = Instancio.of(Transducer.class)
                .set(Select.field("id"), TestConstants.TRANSDUCER_ID.toString()).create();
        Mockito.when(transducerService.findTransducerById(TestConstants.TRANSDUCER_ID.toString()))
                .thenReturn(transducer);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/id/" + TestConstants.TRANSDUCER_ID).build())
                .exchange().expectStatus().isOk();

        Mockito.verify(transducerService, Mockito.times(1)).findTransducerById(TestConstants.TRANSDUCER_ID.toString());
    }

    @Test
    void shouldFindByPtArt() {
        Transducer transducer = Instancio.of(Transducer.class)
                .set(Select.field("art"), TestConstants.PT_ART).create();
        Mockito.when(transducerService.findTransducerByArt(TestConstants.PT_ART)).thenReturn(transducer);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/art/" + TestConstants.PT_ART).build())
                .exchange().expectStatus().isOk();

        Mockito.verify(transducerService, Mockito.times(1)).findTransducerByArt(TestConstants.PT_ART);
    }

    @Test
    void shouldGetAll() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(transducerService, Mockito.times(1)).getAll(ArgumentMatchers.any());
    }

    @Test
    void shouldDelete() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/delete/" + TestConstants.TRANSDUCER_ID).build())
                .exchange().expectStatus().isAccepted();

        Mockito.verify(transducerService, Mockito.times(1)).deleteTransducer(TestConstants.TRANSDUCER_ID.toString());
    }

    @Test
    void shouldUpdate() {
        TransducerDto transducerDto = Instancio.create(TransducerDto.class);
        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/update/" + TestConstants.TRANSDUCER_ID).build())
                .bodyValue(transducerDto).exchange().expectStatus().isAccepted();

        Mockito.verify(transducerService, Mockito.times(1))
                .updateTransducer(TestConstants.TRANSDUCER_ID.toString(), transducerDto);
    }

}