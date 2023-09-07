package ru.veselov.transducersmanagingservice.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.dto.CustomerDto;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class CustomerControllerValidationIntegrationTest extends PostgresContainersConfig {

    private final static String URL_PREFIX = "/api/v1/customer";

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnValidationErrorWithName() {
        CustomerDto customerDto = CustomerDto.builder().name("").inn(TestConstants.INN)
                .build();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("name");
    }

    @Test
    void shouldReturnValidationErrorWithInn() {
        CustomerDto customerDto = CustomerDto.builder().name("Vasya").inn("")
                .build();

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/create").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("inn");
    }

    @Test
    void shouldReturnValidationErrorWithWrongIdFormat() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + "notUUID")
                        .build())
                .exchange().expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("customerId");
    }

    @Test
    void shouldReturnValidationErrorForSortingParamsNotCustomerField() {
        //passed sort parameter for serial number entity to check validation groups
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .queryParam("sort", "number")
                        .build())
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("sort");
    }

    @Test
    void shouldReturnValidationErrorForDeletingWithNotUUID() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete/" + "notUUID")
                        .build()).exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("customerId");
    }

    @Test
    void shouldReturnValidationErrorForUpdatingWithNotUUID() {
        CustomerDto customerDto = CustomerDto.builder().name("name").inn(TestConstants.INN).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + "notUUID")
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("customerId");
    }

    @Test
    void ShouldReturnValidationErrorForUpdatingWithWrongNameDto() {
        CustomerDto customerDto = CustomerDto.builder().name("").inn(TestConstants.INN).build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + TestConstants.CUSTOMER_ID)
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("name");
    }

    @Test
    void ShouldReturnValidationErrorForUpdatingWithWrongInnDto() {
        CustomerDto customerDto = CustomerDto.builder().name("Vasya").inn("").build();

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/" + TestConstants.CUSTOMER_ID)
                        .build())
                .bodyValue(customerDto)
                .exchange().expectStatus().isBadRequest().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("inn");
    }

}
