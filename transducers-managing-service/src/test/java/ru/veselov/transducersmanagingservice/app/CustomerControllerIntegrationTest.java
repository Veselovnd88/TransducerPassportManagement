package ru.veselov.transducersmanagingservice.app;

import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;

import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class CustomerControllerIntegrationTest extends PostgresContainersConfig {

    private final static String URL_PREFIX = "/api/v1/customer";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerRepository customerRepository;

    @AfterEach
    void clear() {
        customerRepository.deleteAll();
    }

    @Test
    void shouldSave() {
        CustomerDto customerDto = Instancio.create(CustomerDto.class);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted();

        Optional<CustomerEntity> foundByInn = customerRepository.findByInn(customerDto.getInn());
        Assertions.assertThat(foundByInn).isPresent();
        CustomerEntity customerEntity = foundByInn.get();
        Assertions.assertThat(customerEntity.getName()).isEqualTo(customerDto.getName());
        Assertions.assertThat(customerEntity.getId()).isNotNull();
    }

    @Test
    void shouldReturnErrorIfCustomerWithInnExists() {
        CustomerEntity customer = saveCustomer();

        CustomerDto customerDto = Instancio.of(CustomerDto.class)
                .set(Select.field("inn"), customer.getInn()).create();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_CONFICT.toString());
    }

    @Test
    void shouldFindById() {
        CustomerEntity customer = saveCustomer();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + customer.getId())
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(customer.getName())
                .jsonPath("$.inn").isEqualTo(customer.getInn())
                .jsonPath("$.id").isEqualTo(customer.getId().toString());
    }

    @Test
    void shouldReturnErrorIfCustomerWithIdNotFound() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + TestConstants.CUSTOMER_ID)
                        .build())
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnCustomerByInn() {
        CustomerEntity customer = saveCustomer();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/inn/" + customer.getInn())
                        .build())
                .exchange().expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo(customer.getName())
                .jsonPath("$.inn").isEqualTo(customer.getInn())
                .jsonPath("$.id").isEqualTo(customer.getId().toString());
    }

    @Test
    void shouldReturnNotFoundErrorInNotCustomerFoundWithInn() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/inn/" + TestConstants.INN)
                        .build())
                .exchange().expectStatus().isNotFound()
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnArrayWithCustomers() {
        saveCustomer();
        customerRepository.save(CustomerEntity.builder().name("Petya").inn("666").build());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all").build())
                .exchange().expectStatus().isOk().expectBody().jsonPath("$").isArray()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].name").value(Matchers.anyOf(Matchers.is("Vasya"), Matchers.is("Petya")));
    }

    @Test
    void shouldDeleteCustomer() {
        CustomerEntity customer = saveCustomer();

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete/" + customer.getId())
                .build()).exchange().expectStatus().isAccepted();

        Optional<CustomerEntity> foundCustomer = customerRepository.findById(customer.getId());
        Assertions.assertThat(foundCustomer).isNotPresent();
    }

    @Test
    void shouldUpdateCustomerWithSameInn() {
        String newName = "new name";
        CustomerEntity customer = saveCustomer();
        CustomerDto customerDto = CustomerDto.builder().name(newName).inn(customer.getInn())
                .build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + customer.getId())
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted().expectBody()
                .jsonPath("$.name").isEqualTo(newName)
                .jsonPath("$.inn").isEqualTo(customer.getInn());
    }

    @Test
    void shouldUpdateCustomerWithNewInn() {
        String newName = "new name";
        CustomerEntity customer = saveCustomer();
        CustomerDto customerDto = CustomerDto.builder().name(newName).inn("666")
                .build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + customer.getId())
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isAccepted().expectBody()
                .jsonPath("$.name").isEqualTo(newName)
                .jsonPath("$.inn").isEqualTo(customerDto.getInn());
    }

    @Test
    void shouldReturnErrorIfCustomerWithSameInnAlreadyExists() {
        CustomerEntity customer = saveCustomer();
        customerRepository.save(CustomerEntity.builder().inn("666").name("Petya").build());
        CustomerDto customerDto = CustomerDto.builder().name("Boba").inn("666")
                .build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + customer.getId())
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isEqualTo(HttpStatus.CONFLICT).expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_CONFICT.toString());
    }

    CustomerEntity saveCustomer() {
        CustomerEntity customerEntity = CustomerEntity.builder().name("Vasya")
                .inn(TestConstants.INN).build();
        return customerRepository.save(customerEntity);
    }

}
