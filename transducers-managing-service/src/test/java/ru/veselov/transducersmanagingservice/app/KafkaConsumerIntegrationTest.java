package ru.veselov.transducersmanagingservice.app;

import org.awaitility.Awaitility;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.transducersmanagingservice.TestConstants;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.dto.SerialNumberDto;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
@Import({KafkaProducerTestConfiguration.class})
@DirtiesContext
class KafkaConsumerIntegrationTest {


    @Autowired
    KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate;

    @Test
    void shouldReceiveMessageInConsumer() {
        kafkaTemplate.send("passports", getGeneratePassportDto());
        Awaitility.await().pollDelay(Duration.of(3, ChronoUnit.SECONDS)).until(() -> true);
    }

    private GeneratePassportsDto getGeneratePassportDto() {
        SerialNumberDto serialNumberDto = new SerialNumberDto("123", UUID.randomUUID().toString());
        SerialNumberDto serialNumberDto2 = new SerialNumberDto("456", UUID.randomUUID().toString());
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .ignore(Select.field("serials"))
                .supply(Select.field(GeneratePassportsDto::getTemplateId), TestConstants.TEMPLATE_ID::toString)
                .create();

        generatePassportsDto.setSerials(List.of(serialNumberDto, serialNumberDto2));
        return generatePassportsDto;
    }

}
