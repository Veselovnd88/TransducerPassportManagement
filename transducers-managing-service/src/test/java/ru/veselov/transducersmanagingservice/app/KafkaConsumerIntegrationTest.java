package ru.veselov.transducersmanagingservice.app;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.app.testcontainers.PostgresContainersConfig;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.dto.SerialNumberDto;
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

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Import({KafkaProducerTestConfiguration.class})
@DirtiesContext
class KafkaConsumerIntegrationTest extends PostgresContainersConfig {

    @Autowired
    KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate;

    @Autowired
    TransducerRepository transducerRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @Autowired
    PassportRepository passportRepository;

    @Autowired
    TemplateRepository templateRepository;

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
        passportRepository.deleteAll();
    }

    @Test
    void shouldReceiveMessageInConsumerAndSaveToDB() {
        SerialNumberEntity serialNumberEntity = saveSerialNumberInRepo(LocalDate.of(2023, 9, 20));
        TemplateEntity templateEntity = saveTemplateInRepo();
        GeneratePassportsDto generatePassportsDto = new GeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(
                new SerialNumberDto(serialNumberEntity.getNumber(), serialNumberEntity.getId().toString())));
        generatePassportsDto.setTemplateId(templateEntity.getId().toString());
        generatePassportsDto.setPrintDate(LocalDate.of(2023, 9, 20));

        kafkaTemplate.send("passports", generatePassportsDto);
        Awaitility.await().pollDelay(Duration.of(3, ChronoUnit.SECONDS)).until(() -> true);

        List<PassportEntity> passportEntities = passportRepository.findAllBetweenDates(LocalDate.of(2023, 9, 20),
                LocalDate.of(2023, 9, 20), PageRequest.of(0, 1, Sort.unsorted())).getContent();
        Assertions.assertThat(passportEntities).hasSize(1);
        PassportEntity passportEntity = passportEntities.get(0);
        Assertions.assertThat(passportEntity.getPrintDate()).isEqualTo(LocalDate.of(2023, 9, 20).toString());
        Assertions.assertThat(passportEntity.getSerialNumber()).isEqualTo(serialNumberEntity);
        Assertions.assertThat(passportEntity.getTemplate()).isEqualTo(templateEntity);
    }

    @Test
    void shouldReceiveMessageInConsumerAndSaveToDBWithNullTemplateField() {
        SerialNumberEntity serialNumberEntity = saveSerialNumberInRepo(LocalDate.of(2023, 9, 20));
        GeneratePassportsDto generatePassportsDto = new GeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(
                new SerialNumberDto(serialNumberEntity.getNumber(), serialNumberEntity.getId().toString())));
        generatePassportsDto.setTemplateId(TestConstants.TEMPLATE_ID.toString());
        generatePassportsDto.setPrintDate(LocalDate.of(2023, 9, 20));

        kafkaTemplate.send("passports", generatePassportsDto);
        Awaitility.await().pollDelay(Duration.of(3, ChronoUnit.SECONDS)).until(() -> true);

        List<PassportEntity> passportEntities = passportRepository.findAllBetweenDates(LocalDate.of(2023, 9, 20),
                LocalDate.of(2023, 9, 20), PageRequest.of(0, 1, Sort.unsorted())).getContent();
        Assertions.assertThat(passportEntities).hasSize(1);
        PassportEntity passportEntity = passportEntities.get(0);
        Assertions.assertThat(passportEntity.getPrintDate()).isEqualTo(LocalDate.of(2023, 9, 20).toString());
        Assertions.assertThat(passportEntity.getSerialNumber()).isEqualTo(serialNumberEntity);
        Assertions.assertThat(passportEntity.getTemplate()).isNull();
    }

    @Test
    void shouldReceiveMessageInConsumerAndAndNotSaveWithNotFoundSerial() {
        GeneratePassportsDto generatePassportsDto = new GeneratePassportsDto();
        generatePassportsDto.setSerials(List.of(
                new SerialNumberDto("123", UUID.randomUUID().toString())));
        generatePassportsDto.setTemplateId(TestConstants.TEMPLATE_ID.toString());
        generatePassportsDto.setPrintDate(LocalDate.of(2023, 9, 20));

        kafkaTemplate.send("passports", generatePassportsDto);
        Awaitility.await().pollDelay(Duration.of(3, ChronoUnit.SECONDS)).until(() -> true);

        List<PassportEntity> passportEntities = passportRepository.findAllBetweenDates(LocalDate.of(2023, 9, 20),
                LocalDate.of(2023, 9, 20), PageRequest.of(0, 1, Sort.unsorted())).getContent();
        Assertions.assertThat(passportEntities).isEmpty();
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

    private TemplateEntity saveTemplateInRepo() {
        TemplateEntity templateEntity = new TemplateEntity(TestConstants.PT_ART,
                "template_name",
                "filename.docx",
                "bucket",
                null,
                true);
        return templateRepository.save(templateEntity);
    }

}
