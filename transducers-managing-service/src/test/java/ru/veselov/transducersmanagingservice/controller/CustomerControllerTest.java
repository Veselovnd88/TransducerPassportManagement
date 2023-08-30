package ru.veselov.transducersmanagingservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.service.CustomerService;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private final String URL_PREFIX = "/api/v1/customer";

    @Mock
    CustomerService customerService;

    @InjectMocks
    CustomerController customerController;

    WebTestClient webTestClient;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(customerController).build();
    }

    @Test
    void shouldSave() {
        CustomerDto customerDto = new CustomerDto("1", "1");
        System.out.println(customerDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted();
        Mockito.verify(customerService, Mockito.times(1)).save(customerDto);
    }

}
