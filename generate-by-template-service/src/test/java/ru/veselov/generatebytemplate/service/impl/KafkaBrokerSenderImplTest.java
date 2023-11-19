package ru.veselov.generatebytemplate.service.impl;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.event.TaskStatus;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class KafkaBrokerSenderImplTest {

    @Mock(name = "passportsDtoKafkaTemplate")
    KafkaTemplate<String, GeneratePassportsDto> passportsDtoKafkaTemplate;

    @Mock(name = "taskResultDtoKafkaTemplate")
    KafkaTemplate<String, TaskResultDto> taskResultDtoKafkaTemplate;

    KafkaBrokerSenderImpl kafkaBrokerSender;

    @BeforeEach
    void init() {
        kafkaBrokerSender = new KafkaBrokerSenderImpl(passportsDtoKafkaTemplate, taskResultDtoKafkaTemplate);
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

    @RepeatedTest(3)
        //flaky test
    void shouldSendTaskResultToKafka() {
        CompletableFuture<SendResult<String, TaskResultDto>> mockCF = CompletableFuture.completedFuture(
                new SendResult<>(Mockito.mock(ProducerRecord.class), Mockito.mock(RecordMetadata.class)
                ));
        TaskResultDto taskResultDto = new TaskResultDto(TestUtils.FILE_ID, "message", null, TaskStatus.PERFORMED);
        Mockito.doReturn(mockCF).when(taskResultDtoKafkaTemplate)
                .send(TestUtils.TASK_TOPIC, TestUtils.TASK_ID_STR, taskResultDto);

        kafkaBrokerSender.sendResultMessage(TestUtils.TASK_ID_STR, taskResultDto);

        Mockito.verify(taskResultDtoKafkaTemplate).send(TestUtils.TASK_TOPIC, TestUtils.TASK_ID_STR, taskResultDto);
    }

}
