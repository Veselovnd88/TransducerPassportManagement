package ru.veselov.taskservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mvc-executor")
@Data
public class MvcTaskExecutorProps {

    private int corePoolSize;

    private int maxPoolSize;

    private int queueCapacity;

}
