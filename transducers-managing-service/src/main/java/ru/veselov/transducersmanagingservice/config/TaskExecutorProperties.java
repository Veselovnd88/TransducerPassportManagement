package ru.veselov.transducersmanagingservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "task-executor")
@Component
@Data
public class TaskExecutorProperties {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;


}
