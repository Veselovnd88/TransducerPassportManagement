package ru.veselov.transducersmanagingservice.app;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.config.KafkaConsumerConfiguration;
import ru.veselov.transducersmanagingservice.entity.PassportEntity;
import ru.veselov.transducersmanagingservice.repository.PassportRepository;
import ru.veselov.transducersmanagingservice.service.PassportStorageService;
import ru.veselov.transducersmanagingservice.service.impl.KafkaGeneratePassportsConsumer;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@DirtiesContext
@ActiveProfiles("test")
public class SchedulingDeleteIntegrationTest extends PostgresContainersConfig {

    @Autowired
    PassportStorageService passportStorageService;

    @Autowired
    PassportRepository passportRepository;

    @MockBean
    KafkaConsumerConfiguration kafkaConsumerConfiguration;

    @MockBean
    KafkaGeneratePassportsConsumer kafkaGeneratePassportsConsumer;


    @Test
    void shouldDeletePassportsWithNullTemplateAndSerialNumber() {
        PassportEntity p1 = new PassportEntity(null, null, LocalDate.of(2023, 9, 20));
        PassportEntity p2 = new PassportEntity(null, null, LocalDate.of(2023, 9, 20));

        passportRepository.save(p1);
        passportRepository.save(p2);
        passportRepository.flush();
        List<PassportEntity> all = passportRepository.findAll();
        Assertions.assertThat(all).hasSize(2);

        passportStorageService.deleteWithNullTemplateAndNullSerialNumber();

        List<PassportEntity> allAfterDelete = passportRepository.findAll();
        Assertions.assertThat(allAfterDelete).isEmpty();
    }


}
