package ru.veselov.generatebytemplate.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@RequiredArgsConstructor
@Slf4j
public class SpringAsyncConfiguration {

    private final ExecutorProperties executorProperties;

    @Bean(name = "asyncThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(executorProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(executorProperties.getQueueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix("gen-xctr");
        threadPoolTaskExecutor.initialize();
        log.trace("Creating ThreadPoolTaskExecutor");
        return threadPoolTaskExecutor;
    }

}
