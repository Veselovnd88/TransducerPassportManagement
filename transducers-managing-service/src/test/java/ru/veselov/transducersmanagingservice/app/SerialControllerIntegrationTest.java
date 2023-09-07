package ru.veselov.transducersmanagingservice.app;

import org.hamcrest.Matchers;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.entity.CustomerEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.exception.error.ErrorCode;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class SerialControllerIntegrationTest extends PostgresContainersConfig {

    private static final String URL_PREFIX = "/api/v1/serials";

    public static final String AFTER = "after";

    public static final String BEFORE = "before";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    TransducerRepository transducerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    TransducerEntity transducerEntity;

    CustomerEntity customerEntity;

    @BeforeEach
    void init() {
        transducerEntity = saveTransducerInRepo();
        customerEntity = saveCustomerEntityInRepo();
    }

    @AfterEach
    void clear() {
        transducerRepository.deleteAll();
        customerRepository.deleteAll();
        serialNumberRepository.deleteAll();
    }

    @Test
    void shouldReturnSerialBetweenDate() {
        SerialNumberEntity todaySerial = saveSerialNumberInRepo(LocalDate.of(2023, 9, 5));
        SerialNumberEntity older = saveSerialNumberInRepo(LocalDate.of(2023, 5, 5));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .queryParam(AFTER, "2023-06-05")
                        .queryParam(BEFORE, "2023-09-05")
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].number").isEqualTo(todaySerial.getNumber());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .queryParam(AFTER, "2023-05-05")
                        .queryParam(BEFORE, "2023-09-05")
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].number").value(Matchers.anyOf(Matchers.is(todaySerial.getNumber()),
                        Matchers.is(older.getNumber())));
    }

    @Test
    void shouldReturnSerialsBetweenDatesByPtArt() {
        SerialNumberEntity todaySerial = saveSerialNumberInRepo(LocalDate.of(2023, 9, 5));
        saveSerialNumberInRepo(LocalDate.of(2023, 5, 5));
        TransducerEntity anotherTransducer = Instancio.create(TransducerEntity.class);
        TransducerEntity savedAnotherTransducer = transducerRepository.save(anotherTransducer);
        SerialNumberEntity savedAnotherSerial = serialNumberRepository.save(Instancio.of(SerialNumberEntity.class)
                .set(Select.field("ptArt"), savedAnotherTransducer.getArt())
                .set(Select.field("customer"), customerEntity)
                .set(Select.field("transducer"), savedAnotherTransducer)
                .set(Select.field("savedAt"), LocalDate.of(2023, 9, 5)).create());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .path("/art/" + TestConstants.PT_ART)
                        .queryParam(AFTER, "2023-06-05")
                        .queryParam(BEFORE, "2023-09-05")
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].number").isEqualTo(todaySerial.getNumber());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .path("/art/" + anotherTransducer.getArt())
                        .queryParam(AFTER, "2023-06-05")
                        .queryParam(BEFORE, "2023-09-05")
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].number").isEqualTo(savedAnotherSerial.getNumber());
    }

    @Test
    void shouldReturnAllSerialsByArt() {
        SerialNumberEntity todaySerial = saveSerialNumberInRepo(LocalDate.of(2023, 9, 5));
        SerialNumberEntity older = saveSerialNumberInRepo(LocalDate.of(2023, 5, 5));
        TransducerEntity anotherTransducer = Instancio.create(TransducerEntity.class);
        TransducerEntity savedAnotherTransducer = transducerRepository.save(anotherTransducer);
        serialNumberRepository.save(Instancio.of(SerialNumberEntity.class)
                .set(Select.field("ptArt"), savedAnotherTransducer.getArt())
                .set(Select.field("customer"), customerEntity)
                .set(Select.field("transducer"), savedAnotherTransducer)
                .set(Select.field("savedAt"), LocalDate.of(2023, 9, 5)).create());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all")
                        .path("/art/" + TestConstants.PT_ART)
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].number").value(Matchers.anyOf(Matchers.is(todaySerial.getNumber()),
                        Matchers.is(older.getNumber())));
    }

    @Test
    void shouldGetSerialNumberByItsNumber() {
        SerialNumberEntity serialNumberEntity = saveSerialNumberInRepo(LocalDate.now());
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/number/" + serialNumberEntity.getNumber()).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(1)
                .jsonPath("$[0].number").isEqualTo(TestConstants.NUMBER)
                .jsonPath("$[0].ptArt").isEqualTo(TestConstants.PT_ART);
    }

    @Test
    void shouldFindSerialNumberById() {
        SerialNumberEntity serialNumberEntity = saveSerialNumberInRepo(LocalDate.now());
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + serialNumberEntity.getId())
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(serialNumberEntity.getId().toString())
                .jsonPath("$.number").isEqualTo(serialNumberEntity.getNumber())
                .jsonPath("$.ptArt").isEqualTo(serialNumberEntity.getPtArt())
                .jsonPath("$.customer").isEqualTo(serialNumberEntity.getCustomer().getName());
    }

    @Test
    void shouldReturnNotFoundErrorIfNoSerialWithId() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/id/" + TestConstants.SERIAL_ID)
                        .build())
                .exchange().expectStatus().isNotFound().expectBody()
                .jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_NOT_FOUND.toString());
    }

    @Test
    void shouldReturnAllSerialsByArtAndCustomerBetweenDates() {
        SerialNumberEntity todaySerial = saveSerialNumberInRepo(LocalDate.of(2023, 9, 5));
        SerialNumberEntity older = saveSerialNumberInRepo(LocalDate.of(2023, 5, 5));
        TransducerEntity anotherTransducer = Instancio.create(TransducerEntity.class);
        TransducerEntity savedAnotherTransducer = transducerRepository.save(anotherTransducer);
        serialNumberRepository.save(Instancio.of(SerialNumberEntity.class)
                .set(Select.field("ptArt"), savedAnotherTransducer.getArt())
                .set(Select.field("customer"), customerEntity)
                .set(Select.field("transducer"), savedAnotherTransducer)
                .set(Select.field("savedAt"), LocalDate.of(2023, 9, 5)).create());

        CustomerEntity anotherCustomer = Instancio.create(CustomerEntity.class);
        CustomerEntity savedCustomer = customerRepository.save(anotherCustomer);
        serialNumberRepository.save(Instancio.of(SerialNumberEntity.class)
                .set(Select.field("ptArt"), TestConstants.PT_ART)
                .set(Select.field("customer"), savedCustomer)
                .set(Select.field("transducer"), transducerEntity)
                .set(Select.field("savedAt"), LocalDate.of(2023, 9, 5)).create());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/all/dates")
                        .path("/art/" + TestConstants.PT_ART)
                        .path("/customer/" + customerEntity.getId())
                        .queryParam(AFTER, "2023-04-05")
                        .queryParam(BEFORE, "2023-09-05")
                        .build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.size()").isEqualTo(2)
                .jsonPath("$[0].number").value(Matchers.anyOf(Matchers.is(todaySerial.getNumber()),
                        Matchers.is(older.getNumber())));
    }


    private TransducerEntity saveTransducerInRepo() {
        return transducerRepository.save(Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create());
    }

    private CustomerEntity saveCustomerEntityInRepo() {
        return customerRepository.save(Instancio.of(CustomerEntity.class)
                .set(Select.field("inn"), TestConstants.INN).create());
    }

    private SerialNumberEntity saveSerialNumberInRepo(LocalDate date) {
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(
                TestConstants.NUMBER,
                TestConstants.PT_ART,
                "comment",
                customerEntity,
                date,
                transducerEntity
        );
        return serialNumberRepository.save(serialNumberEntity);
    }


}
