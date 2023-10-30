package ru.veselov.generatebytemplate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.service.KafkaBrokerSender;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaBrokerSenderImpl implements KafkaBrokerSender {

    @Value("${spring.kafka.passport-topic}")
    private String passportTopic;

    @Value("${spring.kafka.task-topic}")
    private String taskTopic;

    private final KafkaTemplate<String, GeneratePassportsDto> passportsDtoKafkaTemplate;

    private final KafkaTemplate<String, TaskResultDto> taskResultDtoKafkaTemplate;

    @Override
    public void sendResultMessage(String taskId, TaskResultDto taskResultDto) {
        CompletableFuture<SendResult<String, TaskResultDto>> send =
                taskResultDtoKafkaTemplate.send(taskTopic, taskId, taskResultDto);
        send.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Result successfully sent to kafka broker: [topic: {}, message: {}]",
                        taskTopic, result.getProducerRecord().value());
            } else {
                log.error("Result wasn't sent to kafka broker with error: {}", ex.getMessage());
            }
        });
    }

    @Override
    public void sendPassportInfoMessage(GeneratePassportsDto generatePassportsDto) {
        Message<GeneratePassportsDto> message = MessageBuilder.withPayload(generatePassportsDto)
                .setHeader(KafkaHeaders.TOPIC, passportTopic).build();
        CompletableFuture<SendResult<String, GeneratePassportsDto>> send =
                passportsDtoKafkaTemplate.send(message);
        send.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Message successfully sent to Kafka broker, to [topic {}: message: {}]",
                        passportTopic, generatePassportsDto);
            } else {
                log.error("Message was not sent to broker with exception: {}", ex.getMessage());
            }
        });
    }

}
