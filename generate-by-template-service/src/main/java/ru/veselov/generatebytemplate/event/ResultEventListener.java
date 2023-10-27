package ru.veselov.generatebytemplate.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.dto.TaskResultDto;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultEventListener {

    @Value("${spring.kafka.task-topic}")
    private String taskTopic;

    private final KafkaTemplate<String, TaskResultDto> kafkaTemplate;

    @EventListener
    public void handleSuccessResultEvent(SuccessResultEvent resultEvent) {
        TaskResultDto taskResultDto = new TaskResultDto(
                resultEvent.getResultFileId(),
                resultEvent.getMessage(),
                null,
                resultEvent.getEventType()
        );
        kafkaTemplate.send(taskTopic, resultEvent.getTaskId().toString(), taskResultDto);
        log.info("Send result of task to task service: " + resultEvent);
    }

    @EventListener
    public void handleErrorResultEvent(ErrorResultEvent resultEvent) {
        TaskResultDto taskResultDto = new TaskResultDto(
                null,
                resultEvent.getMessage(),
                resultEvent.getErrorMessage(),
                resultEvent.getEventType()
        );
        kafkaTemplate.send(taskTopic, resultEvent.getTaskId().toString(), taskResultDto);
        log.info("Send error of task to task service: " + resultEvent);
    }

}
