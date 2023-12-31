package ru.veselov.transducersmanagingservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.service.PassportStorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaGeneratePassportsConsumer {

    private final PassportStorageService passportStorageService;

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        log.info("Received message from Kafka broker: [topic: {}, group: {}", "passports", "passport]");
        passportStorageService.save(generatePassportsDto);
        log.debug("Message from Kafka broker successfully forwarded to service");
    }

}
