package ru.veselov.transducersmanagingservice.controller;

import org.instancio.Instancio;
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
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.model.Customer;
import ru.veselov.transducersmanagingservice.service.CustomerService;

import java.util.List;

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
        CustomerDto customerDto = Instancio.create(CustomerDto.class);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted();
        Mockito.verify(customerService, Mockito.times(1)).save(customerDto);
    }

    @Test
    void shouldReturnCustomerById() {
        Customer customer = Instancio.create(Customer.class);
        Mockito.when(customerService.findCustomerById(TestConstants.CUSTOMER_ID.toString()))
                .thenReturn(customer);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + TestConstants.CUSTOMER_ID)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody(Customer.class);

        Mockito.verify(customerService, Mockito.times(1)).findCustomerById(TestConstants.CUSTOMER_ID.toString());
    }

    @Test
    void shouldReturnCustomerByInn() {
        Customer customer = Instancio.create(Customer.class);
        Mockito.when(customerService.findCustomerByInn(TestConstants.INN))
                .thenReturn(customer);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/inn/" + TestConstants.INN)
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody(Customer.class);

        Mockito.verify(customerService, Mockito.times(1)).findCustomerByInn(TestConstants.INN);
    }

    @Test
    void shouldReturnAllCustomers() {
        Mockito.when(customerService.getAllCustomers(ArgumentMatchers.any()))
                .thenReturn(List.of(Instancio.create(Customer.class)));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").build())
                .exchange().expectStatus().isOk().expectBody(List.class);

        Mockito.verify(customerService, Mockito.times(1)).getAllCustomers(ArgumentMatchers.any());
    }

    @Test
    void shouldDeleteCustomer() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete/id/" + TestConstants.CUSTOMER_ID)
                .build()).exchange().expectStatus().isAccepted();

        Mockito.verify(customerService, Mockito.times(1)).deleteCustomer(TestConstants.CUSTOMER_ID.toString());
    }

    @Test
    void shouldUpdateCustomer() {
        CustomerDto customerDto = Instancio.create(CustomerDto.class);
        Mockito.when(customerService.updateCustomer(TestConstants.CUSTOMER_ID.toString(), customerDto))
                .thenReturn(Instancio.create(Customer.class));

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/id/" + TestConstants.CUSTOMER_ID)
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted().expectBody(Customer.class);

        Mockito.verify(customerService, Mockito.times(1))
                .updateCustomer(TestConstants.CUSTOMER_ID.toString(), customerDto);
    }

}
