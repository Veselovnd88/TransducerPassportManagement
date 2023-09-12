package ru.veselov.passportprocessing.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final ObservationRegistry observationRegistry;

    @Bean
    public WebClient webClient() {
        return webClientBuilder().build();
    }

    @Bean
    @Qualifier("simpleWebClient")
    public WebClient simpleWebClient() {
        return WebClient.builder().observationRegistry(observationRegistry).build();
    }

    @LoadBalanced
    @Bean
    WebClient.Builder webClientBuilder() {
        return WebClient.builder().observationRegistry(observationRegistry);
    }

}
