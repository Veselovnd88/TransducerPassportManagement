package ru.veselov.generatebytemplate.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.dto.TaskResultDto;
import ru.veselov.generatebytemplate.service.KafkaBrokerSender;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultEventListener {

    private final KafkaBrokerSender kafkaBrokerSender;

    @EventListener
    public void handleSuccessResultEvent(SuccessResultEvent resultEvent) {
        TaskResultDto taskResultDto = new TaskResultDto(
                resultEvent.getResultFileId(),
                resultEvent.getMessage(),
                null,
                resultEvent.getEventType()
        );
        kafkaBrokerSender.sendResultMessage(resultEvent.getTaskId().toString(), taskResultDto);
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
        kafkaBrokerSender.sendResultMessage(resultEvent.getTaskId().toString(), taskResultDto);
        log.info("Send error of task to task service: " + resultEvent);
    }

}
