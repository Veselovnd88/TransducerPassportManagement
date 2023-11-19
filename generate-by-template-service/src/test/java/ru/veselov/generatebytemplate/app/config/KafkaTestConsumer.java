package ru.veselov.generatebytemplate.app.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;

@Getter
@Component
@Slf4j
public class KafkaTestConsumer {

    private GeneratePassportsDto listenedPassportsDto;

    private TaskResultDto listenedTaskResultDto;

    @KafkaListener(groupId = "passport", topics = "passports", containerFactory = "passportListenerFactory")
    public void listen(GeneratePassportsDto generatePassportsDto) {
        log.debug("Consumer received message from broker: {}", generatePassportsDto);
        listenedPassportsDto = generatePassportsDto;
    }

    @KafkaListener(groupId = "task", topics = "task", containerFactory = "taskListenerFactory")
    public void listen(TaskResultDto taskResultDto) {
        log.debug("Consumer received message from broker: {}", taskResultDto);
        listenedTaskResultDto = taskResultDto;
    }

}
