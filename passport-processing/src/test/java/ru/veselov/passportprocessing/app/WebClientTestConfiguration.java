package ru.veselov.passportprocessing.app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

@TestConfiguration
public class WebClientTestConfiguration {

    @Bean
    @Primary
    public WebClient webClientTest() {
        return webClientBuilderTest().build();
    }

    @Bean
    @Primary
    WebClient.Builder webClientBuilderTest() {
        return WebClient.builder();
    }

}
