package ru.veselov.passportprocessing.app;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;

@Component
public class KafkaTestConsumer {

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")

    public void listen(GeneratePassportsDto generatePassportsDto) {
        System.out.println("Message received: " + generatePassportsDto);
    }
}
