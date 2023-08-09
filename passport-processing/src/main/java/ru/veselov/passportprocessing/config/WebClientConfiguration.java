package ru.veselov.passportprocessing.config;

import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private final ObservationRegistry observationRegistry;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().observationRegistry(observationRegistry).build();
    }


}
