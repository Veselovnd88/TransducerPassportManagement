package ru.veselov.transducersmanagingservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.transducersmanagingservice.TestUtils;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.service.PassportSavingService;

@ExtendWith(MockitoExtension.class)
class KafkaGeneratePassportsConsumerTest {

    @Mock
    PassportSavingService passportSavingService;

    @InjectMocks
    KafkaGeneratePassportsConsumer kafkaGeneratePassportsConsumer;

    @Test
    void shouldPassDtoToService() {
        GeneratePassportsDto generatePassportDtoWithRandomSerials = TestUtils.getGeneratePassportDtoWithRandomSerials();
        kafkaGeneratePassportsConsumer.listen(generatePassportDtoWithRandomSerials);

        Mockito.verify(passportSavingService, Mockito.times(1)).save(generatePassportDtoWithRandomSerials);
    }

}