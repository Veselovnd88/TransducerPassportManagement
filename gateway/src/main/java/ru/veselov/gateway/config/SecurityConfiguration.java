package ru.veselov.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
