package ru.veselov.generatebytemplate.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.model.ResultFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResultEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishSuccessResultEvent(ResultFile resultFile) {
        SuccessResultEvent successResultEvent = new SuccessResultEvent(resultFile.getId().toString(),
                "File was successfully generated");
        publisher.publishEvent(successResultEvent);
        log.info("SuccessResult Event published for [file: {}]", successResultEvent.getResultFileId());
    }

    public void publishErrorResultEvent(Exception exception) {
        ErrorResultEvent errorResultEvent = new ErrorResultEvent(exception.getMessage(),
                "File was not generated due to error");
        publisher.publishEvent(errorResultEvent);
        log.info("ErrorResult Event published for [error: {}]", errorResultEvent.getErrorMessage());
    }

}
