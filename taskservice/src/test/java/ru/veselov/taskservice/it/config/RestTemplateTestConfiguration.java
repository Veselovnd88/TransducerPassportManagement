package ru.veselov.taskservice.it.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import ru.veselov.taskservice.configuration.RestTemplateResponseErrorHandler;

/*
Configure rest template without load balancing and using eureka
**/
@TestConfiguration
@RequiredArgsConstructor
public class RestTemplateTestConfiguration {

    private final RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
        return restTemplate;
    }

}
