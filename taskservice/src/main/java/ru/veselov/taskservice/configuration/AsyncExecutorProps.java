package ru.veselov.taskservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "async-executor")
@Data
public class AsyncExecutorProps {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

}
