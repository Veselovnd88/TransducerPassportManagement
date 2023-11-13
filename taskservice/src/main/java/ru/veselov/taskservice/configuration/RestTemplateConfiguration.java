package ru.veselov.taskservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestTemplateConfiguration {

    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
        log.info("Configuring RestTemplate");
        return restTemplate;
    }

}
