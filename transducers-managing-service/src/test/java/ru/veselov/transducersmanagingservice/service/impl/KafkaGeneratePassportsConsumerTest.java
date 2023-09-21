package ru.veselov.transducersmanagingservice.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.veselov.transducersmanagingservice.TestUtils;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.service.PassportStorageService;

@ExtendWith(MockitoExtension.class)
class KafkaGeneratePassportsConsumerTest {

    @Mock
    PassportStorageService passportStorageService;

    @InjectMocks
    KafkaGeneratePassportsConsumer kafkaGeneratePassportsConsumer;

    @Test
    void shouldPassDtoToService() {
        GeneratePassportsDto generatePassportDtoWithRandomSerials = TestUtils.getGeneratePassportDtoWithRandomSerials();
        kafkaGeneratePassportsConsumer.listen(generatePassportDtoWithRandomSerials);

        Mockito.verify(passportStorageService, Mockito.times(1)).save(generatePassportDtoWithRandomSerials);
    }

}