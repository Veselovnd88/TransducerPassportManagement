package ru.veselov.transducersmanagingservice.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.SerialsDto;
import ru.veselov.transducersmanagingservice.model.SerialNumber;
import ru.veselov.transducersmanagingservice.service.SerialNumberService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SerialNumberControllerTest {

    private final static String URL_PREFIX = "/api/v1/serials";

    private final static byte[] BYTES = new byte[]{1, 2, 3};

    @Mock
    SerialNumberService serialNumberService;

    @InjectMocks
    SerialNumberController serialNumberController;

    WebTestClient webTestClient;

    @Captor
    ArgumentCaptor<MultipartFile> multipartFileArgumentCaptor;

    @Captor
    ArgumentCaptor<SerialsDto> serialsDtoArgumentCaptor;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(serialNumberController).build();
    }

    @Test
    @SneakyThrows
    void shouldSaveSerials() {
        SerialsDto serialsDto = Instancio.of(SerialsDto.class)
                .set(Select.field("customerId"), TestConstants.CUSTOMER_ID.toString()).create();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part("serials", serialsDto);
        multipartBodyBuilder.part("file", BYTES).filename("serials.xlsx");


        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Mockito.verify(serialNumberService, Mockito.times(1))
                .saveSerials(serialsDtoArgumentCaptor.capture(), multipartFileArgumentCaptor.capture());
        SerialsDto capturedSerials = serialsDtoArgumentCaptor.getValue();
        Assertions.assertThat(capturedSerials).isEqualTo(serialsDto);
        MultipartFile capturedMultipart = multipartFileArgumentCaptor.getValue();
        Assertions.assertThat(capturedMultipart.getBytes()).isEqualTo(BYTES);
        Assertions.assertThat(capturedMultipart.getOriginalFilename()).isEqualTo("serials.xlsx");
    }

    @Test
    void shouldGetAllSerialsBetweenDates() {
        Mockito.when(serialNumberService.findBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.DATE_PARAMS))
                .thenReturn(List.of(Instancio.create(SerialNumber.class)));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .queryParams(getQueryParamsWithDateParams())
                        .build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(serialNumberService, Mockito.times(1))
                .findBetweenDates(TestConstants.SORTING_PARAMS, TestConstants.DATE_PARAMS);
    }

    @Test
    void shouldGetAllSerialNumbersByPtArtBetweenDates() {
        Mockito.when(serialNumberService.findByPtArtBetweenDates(
                        TestConstants.SORTING_PARAMS,
                        TestConstants.PT_ART,
                        TestConstants.DATE_PARAMS))
                .thenReturn(List.of(Instancio.create(SerialNumber.class)));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .path("/art/" + TestConstants.PT_ART)
                        .queryParams(getQueryParamsWithDateParams()).build())
                .exchange().expectStatus().isOk()
                .expectBody(List.class);

        Mockito.verify(serialNumberService, Mockito.times(1))
                .findByPtArtBetweenDates(TestConstants.SORTING_PARAMS,
                        TestConstants.PT_ART,
                        TestConstants.DATE_PARAMS);
    }

    @Test
    void shouldGetSerialsByNumber() {
        Mockito.when(serialNumberService.findByNumber(TestConstants.NUMBER))
                .thenReturn(List.of(Instancio.create(SerialNumber.class)));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/number/" + TestConstants.NUMBER).build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(serialNumberService, Mockito.times(1)).findByNumber(TestConstants.NUMBER);
    }

    @Test
    void shouldFindAllSerialsByArt() {
        Mockito.when(serialNumberService.findByArt(TestConstants.SORTING_PARAMS, TestConstants.PT_ART))
                .thenReturn(List.of(Instancio.create(SerialNumber.class)));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").path("/art/" + TestConstants.PT_ART)
                        .queryParams(getQuerySortingParamsOnly()).build())
                .exchange().expectStatus().isOk()
                .expectBody(List.class);

        Mockito.verify(serialNumberService, Mockito.times(1))
                .findByArt(TestConstants.SORTING_PARAMS, TestConstants.PT_ART);
    }

    @Test
    void shouldFindSerialById() {
        Mockito.when(serialNumberService.findById(TestConstants.SERIAL_ID.toString()))
                .thenReturn(Instancio.create(SerialNumber.class));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + TestConstants.SERIAL_ID)
                        .build())
                .exchange().expectStatus().isOk().expectBody(SerialNumber.class);

        Mockito.verify(serialNumberService, Mockito.times(1)).findById(TestConstants.SERIAL_ID.toString());
    }

    @Test
    void shouldFindSerialsByArtAndCustomerBetweenDates() {
        Mockito.when(serialNumberService.findByArtAndCustomerBetweenDates(
                        TestConstants.SORTING_PARAMS,
                        TestConstants.PT_ART,
                        TestConstants.CUSTOMER_ID.toString(),
                        TestConstants.DATE_PARAMS))
                .thenReturn(List.of(Instancio.create(SerialNumber.class)));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .path("/art/" + TestConstants.PT_ART)
                        .path("/customer/" + TestConstants.CUSTOMER_ID)
                        .queryParams(getQueryParamsWithDateParams()).build())
                .exchange().expectStatus().isOk()
                .expectBody(List.class);

        Mockito.verify(serialNumberService, Mockito.times(1))
                .findByArtAndCustomerBetweenDates(TestConstants.SORTING_PARAMS,
                        TestConstants.PT_ART,
                        TestConstants.CUSTOMER_ID.toString(),
                        TestConstants.DATE_PARAMS);
    }

    @Test
    void shouldDeleteSerialById() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/" + TestConstants.SERIAL_ID).build())
                .exchange().expectStatus().isAccepted();

        Mockito.verify(serialNumberService, Mockito.times(1)).deleteSerial(TestConstants.SERIAL_ID.toString());
    }

    private MultiValueMap<String, String> getQueryParamsWithDateParams() {
        MultiValueMap<String, String> linkedMultiValueMap = getQuerySortingParamsOnly();
        linkedMultiValueMap.add(TestConstants.AFTER, TestConstants.DATE_AFTER.toString());
        linkedMultiValueMap.add(TestConstants.BEFORE, TestConstants.DATE_BEFORE.toString());
        return linkedMultiValueMap;
    }

    private MultiValueMap<String, String> getQuerySortingParamsOnly() {
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
        linkedMultiValueMap.add(TestConstants.PAGE, TestConstants.SORTING_PARAMS.getPage().toString());
        linkedMultiValueMap.add(TestConstants.SORT, TestConstants.SORTING_PARAMS.getSort());
        linkedMultiValueMap.add(TestConstants.ORDER, TestConstants.SORTING_PARAMS.getOrder());
        return linkedMultiValueMap;
    }

}