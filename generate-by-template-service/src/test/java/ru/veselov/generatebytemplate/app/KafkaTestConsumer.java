package ru.veselov.generatebytemplate.app;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;

@Getter
@Component
@Slf4j
public class KafkaTestConsumer {

    private GeneratePassportsDto listenedResult;

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "listenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        log.debug("Consumer received message from broker: {}", generatePassportsDto);
        listenedResult = generatePassportsDto;
    }

}
