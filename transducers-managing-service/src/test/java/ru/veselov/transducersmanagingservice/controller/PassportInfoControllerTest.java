package ru.veselov.transducersmanagingservice.controller;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.model.Passport;
import ru.veselov.transducersmanagingservice.service.PassportInfoService;
import ru.veselov.transducersmanagingservice.TestUtils;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PassportInfoControllerTest {

    public static final String URL_PREFIX = "/api/v1/passport";

    public static Passport passport;

    @Mock
    PassportInfoService passportInfoService;

    @InjectMocks
    PassportInfoController passportInfoController;

    WebTestClient webTestClient;

    @BeforeAll
    static void initStatic() {
        passport = Instancio.create(Passport.class);
    }

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(passportInfoController)
                .configureClient().baseUrl(URL_PREFIX)
                .build();
    }

    @Test
    void shouldGetPassportById() {
        Mockito.when(passportInfoService.getById(TestConstants.PASSPORT_ID.toString())).thenReturn(passport);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/id/" + TestConstants.PASSPORT_ID).build())
                .exchange().expectStatus().isOk().expectBody(Passport.class);

        Mockito.verify(passportInfoService, Mockito.times(1)).getById(TestConstants.PASSPORT_ID.toString());
    }

    @Test
    void shouldGetAllBySerialNumber() {
        Mockito.when(passportInfoService.getAllForSerialBetweenDates(
                TestConstants.NUMBER,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS)).thenReturn(List.of(passport));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/all/serial/" + TestConstants.NUMBER)
                        .queryParams(TestUtils.getQueryParamsWithDateParams()).build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(passportInfoService, Mockito.times(1)).getAllForSerialBetweenDates(
                TestConstants.NUMBER,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS);
    }

    @Test
    void shouldGetAllByPtArt() {
        Mockito.when(passportInfoService.getAllForPtArtBetweenDates(
                TestConstants.PT_ART,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS)).thenReturn(List.of(passport));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/all/ptArt/" + TestConstants.PT_ART)
                        .queryParams(TestUtils.getQueryParamsWithDateParams()).build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(passportInfoService, Mockito.times(1)).getAllForPtArtBetweenDates(
                TestConstants.PT_ART,
                TestConstants.SORTING_PARAMS,
                TestConstants.DATE_PARAMS);
    }

    @Test
    void shouldDeletePassportById() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path("/delete/id/" + TestConstants.PASSPORT_ID).build())
                .exchange().expectStatus().isAccepted();

        Mockito.verify(passportInfoService, Mockito.times(1)).deleteById(TestConstants.PASSPORT_ID.toString());
    }

}
