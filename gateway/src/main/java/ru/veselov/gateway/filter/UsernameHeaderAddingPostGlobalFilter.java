package ru.veselov.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class UsernameHeaderAddingPostGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Mono<Authentication> authenticationMono = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
        Mono<String> map = authenticationMono.map(x -> ((Jwt) x.getPrincipal()).getClaimAsString("preferred_username"));
        log.info("Added username header: {}", exchange.getRequest());
        Mono<ServerHttpRequest> username = map.map(x -> exchange.getRequest().mutate().header("username", x).build());
        return username.flatMap(x -> chain.filter(exchange.mutate().request(x).build()));

    }

    @Override
    public int getOrder() {
        return -1;
    }
}
