package ru.veselov.passportprocessing.app;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.veselov.passportprocessing.dto.GeneratePassportsDto;

@Component
public class KafkaTestConsumer {
    private GeneratePassportsDto listenedResult;

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        System.out.println("Consumer received message from broker: " + generatePassportsDto);
        listenedResult = generatePassportsDto;
    }

    public GeneratePassportsDto getListenedResult() {
        return listenedResult;
    }

}
