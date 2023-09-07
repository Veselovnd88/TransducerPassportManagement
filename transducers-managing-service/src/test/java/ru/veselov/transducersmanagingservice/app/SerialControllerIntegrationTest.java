package ru.veselov.transducersmanagingservice.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
class SerialControllerIntegrationTest extends PostgresContainersConfig {

    private final static String URL_PREFIX = "/api/v1/serials";

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



    private TransducerEntity saveTransducerInRepo() {
        return transducerRepository.save(Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create());
    }

    private CustomerEntity saveCustomerEntityInRepo() {
        return customerRepository.save(Instancio.of(CustomerEntity.class)
                .set(Select.field("inn"), TestConstants.INN).create());
    }

    private SerialNumberEntity saveSerialNumberInRepo() {
        SerialNumberEntity serialNumberEntity = new SerialNumberEntity(
                TestConstants.NUMBER,
                TestConstants.PT_ART,
                "comment",
                customerEntity,
                LocalDate.now(),
                transducerEntity
        );
        return serialNumberRepository.save(serialNumberEntity);
    }


}
