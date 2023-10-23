package ru.veselov.generatebytemplate.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ResultEventListener {

    @EventListener
    public void handleResultEvent(ResultEvent resultEvent) {
        log.info("Send result of task to task service: "+ resultEvent);
    }

}
