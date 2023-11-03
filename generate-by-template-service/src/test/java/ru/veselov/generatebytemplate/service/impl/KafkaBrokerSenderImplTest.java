package ru.veselov.generatebytemplate.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.event.EventType;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class KafkaBrokerSenderImplTest {

    @Mock(name = "passportsDtoKafkaTemplate")
    KafkaTemplate<String, GeneratePassportsDto> passportsDtoKafkaTemplate;

    @Mock(name = "taskResultDtoKafkaTemplate")
    KafkaTemplate<String, TaskResultDto> taskResultDtoKafkaTemplate;

    @InjectMocks
    KafkaBrokerSenderImpl kafkaBrokerSender;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(kafkaBrokerSender, "taskTopic", TestUtils.TASK_TOPIC, String.class);
        ReflectionTestUtils.setField(kafkaBrokerSender, "passportTopic", TestUtils.PASSPORT_TOPIC, String.class);
    }

    @Test
    void shouldSendPassportInfoMessageToKafka() {
        CompletableFuture<SendResult<String, GeneratePassportsDto>> mockCF = Mockito.mock(CompletableFuture.class);
        GeneratePassportsDto generatePassportsDto = TestUtils.getBasicGeneratePassportsDto();
        Mockito.when(passportsDtoKafkaTemplate.send(TestUtils.PASSPORT_TOPIC, generatePassportsDto))
                .thenReturn(mockCF);

        kafkaBrokerSender.sendPassportInfoMessage(generatePassportsDto);

        Mockito.verify(passportsDtoKafkaTemplate).send(TestUtils.PASSPORT_TOPIC, generatePassportsDto);
    }

    @Test
    void shouldSendTaskResultToKafka() {
        CompletableFuture<SendResult<String, TaskResultDto>> mockCF = Mockito.mock(CompletableFuture.class);
        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID, "message", null, EventType.READY);
        Mockito.when(taskResultDtoKafkaTemplate.send(TestUtils.TASK_TOPIC,
                TestUtils.TASK_ID, taskResultDto)).thenReturn(mockCF);

        kafkaBrokerSender.sendResultMessage(TestUtils.TASK_ID, taskResultDto);

        Mockito.verify(taskResultDtoKafkaTemplate).send(TestUtils.TASK_TOPIC, TestUtils.TASK_ID, taskResultDto);
    }

}
