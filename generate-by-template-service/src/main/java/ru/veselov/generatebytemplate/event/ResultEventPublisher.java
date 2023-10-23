package ru.veselov.generatebytemplate.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;

@Component
@RequiredArgsConstructor
public class ResultEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishSuccessResultEvent(GeneratedResultFile resultFile) {
        SuccessResultEvent successResultEvent = new SuccessResultEvent(resultFile.getId().toString(),
                "File was successfully generated");
        publisher.publishEvent(successResultEvent);
    }

    public void publishErrorResultEvent(Exception exception) {
        ErrorResultEvent errorResultEvent = new ErrorResultEvent(exception.getMessage(),
                "File was not generated due to error");
        publisher.publishEvent(errorResultEvent);
    }

}
