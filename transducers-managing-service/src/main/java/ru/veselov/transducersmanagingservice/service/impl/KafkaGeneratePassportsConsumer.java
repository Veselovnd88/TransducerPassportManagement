package ru.veselov.transducersmanagingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaGeneratePassportsConsumer {

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        log.info("Received message from Kafka broker: [topic: {}, group: {}", "passports", "passport");
        //TODO to passport service to save async
    }

}
