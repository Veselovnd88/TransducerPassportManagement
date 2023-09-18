package ru.veselov.transducersmanagingservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;

@Service
@RequiredArgsConstructor
public class KafkaGeneratePassportsConsumer {

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        System.out.println("Consumer received message from broker: " + generatePassportsDto);
    }
}
