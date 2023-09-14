package ru.veselov.transducersmanagingservice.app;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
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
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.entity.SerialNumberEntity;
import ru.veselov.transducersmanagingservice.entity.TemplateEntity;
import ru.veselov.transducersmanagingservice.entity.TransducerEntity;
import ru.veselov.transducersmanagingservice.repository.CustomerRepository;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.repository.SerialNumberRepository;
import ru.veselov.transducersmanagingservice.repository.TemplateRepository;
import ru.veselov.transducersmanagingservice.repository.TransducerRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DirtiesContext
public class PassportInfoControllerIntegrationTest extends PostgresContainersConfig {

    public static final String URL_PREFIX = "/api/v1/passport";

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    TransducerRepository transducerRepository;

    @Autowired
    TemplateRepository templateRepository;

    @Autowired
    PassportRepository passportRepository;

    @Autowired
    SerialNumberRepository serialNumberRepository;


    @AfterEach
    void clear() {
        passportRepository.deleteAll();
        serialNumberRepository.deleteAll();
        transducerRepository.deleteAll();
        customerRepository.deleteAll();
        templateRepository.deleteAll();
    }

    @Test
    void shouldReturnPassportById() {
        PassportEntity passportEntity = savePassportToRepo();
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX)
                        .path("/id/" + passportEntity.getId()).build())
                .exchange().expectStatus().isOk().expectBody()
                .jsonPath("$.id").isEqualTo(passportEntity.getId().toString())
                .jsonPath("$.templateId").isEqualTo(passportEntity.getTemplate().getId().toString())
                .jsonPath("$.serial").isEqualTo(passportEntity.getSerialNumber().getNumber())
                .jsonPath("$.ptArt").isEqualTo(passportEntity.getSerialNumber().getPtArt())
                .jsonPath("$.printDate").isEqualTo(passportEntity.getPrintDate().toString());
    }

    private TemplateEntity saveTemplateToRepo() {
        return templateRepository.save(
                TemplateEntity.builder()
                        .bucket("bucket")
                        .templateName("name")
                        .ptArt(TestConstants.PT_ART)
                        .filename("filename").build());
    }

    private SerialNumberEntity saveSerialNumberToRepo() {
        return serialNumberRepository.save(
                SerialNumberEntity.builder()
                        .number(TestConstants.NUMBER)
                        .ptArt(TestConstants.PT_ART)
                        .transducer(saveTransducerToRepo())
                        .customer(saveCustomerToRepo()).build());
    }

    private TransducerEntity saveTransducerToRepo() {
        return transducerRepository.save(Instancio.of(TransducerEntity.class)
                .set(Select.field("art"), TestConstants.PT_ART).create());
    }

    private CustomerEntity saveCustomerToRepo() {
        return customerRepository.save(
                CustomerEntity.builder().name("vasya").inn(TestConstants.INN).build()
        );
    }

    private PassportEntity savePassportToRepo() {
        return passportRepository.save(
                PassportEntity.builder()
                        .printDate(TestConstants.DATE_BEFORE)
                        .serialNumber(saveSerialNumberToRepo())
                        .template(saveTemplateToRepo())
                        .build()
        );
    }


}
