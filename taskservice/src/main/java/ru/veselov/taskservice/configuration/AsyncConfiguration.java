package ru.veselov.taskservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
@RequiredArgsConstructor
@Slf4j
public class AsyncConfiguration {

    private final AsyncExecutorProps asyncExecutorProps;

    @Bean(name = "asyncThreadPoolTaskExecutor")
    @Qualifier("asyncThreadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(asyncExecutorProps.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(asyncExecutorProps.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(asyncExecutorProps.getQueueCapacity());
        threadPoolTaskExecutor.setThreadNamePrefix("async-");
        threadPoolTaskExecutor.initialize();
        log.trace("Creating AsyncThreadPoolTaskExecutor");
        return threadPoolTaskExecutor;
    }

    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster(@Qualifier("asyncThreadPoolTaskExecutor")
                                                                   Executor executor) {
        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        multicaster.setTaskExecutor(executor);
        return multicaster;
    }

}
