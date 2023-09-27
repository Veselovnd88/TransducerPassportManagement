package ru.veselov.gateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ru.veselov.gateway.security.GatewayAccessDeniedHandler;
import ru.veselov.gateway.security.GatewayAuthenticationEntryPoint;
import ru.veselov.gateway.security.GrantedAuthoritiesExtractor;
import ru.veselov.gateway.security.SecurityConstant;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    public static final String ADMIN = "ADMIN";

    public static final String USER = "USER";

    public static final String CUSTOMER = "CUSTOMER";

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec

                        .pathMatchers(HttpMethod.GET, SecurityConstant.TEMPLATE_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.DELETE, SecurityConstant.TEMPLATE_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, SecurityConstant.TEMPLATE_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, SecurityConstant.TEMPLATE_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, SecurityConstant.GENERATE_PASSPORT_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.GET, SecurityConstant.CUSTOMER_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.DELETE, SecurityConstant.CUSTOMER_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.PUT, SecurityConstant.CUSTOMER_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.POST, SecurityConstant.CUSTOMER_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.GET, SecurityConstant.PASSPORT_URL).hasAnyRole(ADMIN, USER, CUSTOMER)
                        .pathMatchers(HttpMethod.DELETE, SecurityConstant.PASSPORT_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, SecurityConstant.SERIALS_URL).authenticated()
                        .pathMatchers(HttpMethod.POST, SecurityConstant.SERIALS_URL).hasAnyRole(USER, ADMIN)
                        .pathMatchers(HttpMethod.DELETE, SecurityConstant.SERIALS_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.GET, SecurityConstant.TRANSDUCER_URL).authenticated()
                        .pathMatchers(HttpMethod.DELETE, SecurityConstant.TRANSDUCER_URL).hasRole(ADMIN)
                        .pathMatchers(HttpMethod.POST, SecurityConstant.TRANSDUCER_URL).hasAnyRole(ADMIN, USER)
                        .pathMatchers(HttpMethod.PUT, SecurityConstant.TRANSDUCER_URL).hasRole(ADMIN)

                        .pathMatchers("/eureka").hasAnyRole(ADMIN)
                        .pathMatchers("/zipkin").hasAnyRole(ADMIN)

                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new GatewayAuthenticationEntryPoint())
                        .accessDeniedHandler(new GatewayAccessDeniedHandler()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
