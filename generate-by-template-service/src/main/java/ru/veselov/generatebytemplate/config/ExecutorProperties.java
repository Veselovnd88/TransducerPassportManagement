package ru.veselov.generatebytemplate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "executor")
@Data
public class ExecutorProperties {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

}
