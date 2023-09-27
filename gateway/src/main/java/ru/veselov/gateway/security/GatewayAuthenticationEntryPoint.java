package ru.veselov.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.veselov.gateway.exception.ApiErrorResponse;
import ru.veselov.gateway.exception.ErrorCode;

@Slf4j
public class GatewayAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        ObjectMapper objectMapper = new ObjectMapper();
        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");

        ApiErrorResponse errorResponse = createErrorResponse(exception);
        exchange.getResponse().getHeaders().add("WWW-Authenticate", computeWWWAuthenticateMessage(errorResponse));
        byte[] messageBytes;
        try {
            messageBytes = objectMapper.writeValueAsBytes(errorResponse);
        } catch (JsonProcessingException e) {
            messageBytes = "Can't create correct error message".getBytes();
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(messageBytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    private static ApiErrorResponse createErrorResponse(AuthenticationException exception) {
        if (exception instanceof InvalidBearerTokenException) {
            if (exception.getMessage().startsWith("Jwt expired")) {
                return new ApiErrorResponse(ErrorCode.ERROR_EXPIRED, exception.getMessage());
            }
        }
        return new ApiErrorResponse(ErrorCode.ERROR_UNAUTHENTICATED, exception.getMessage());
    }

    private static String computeWWWAuthenticateMessage(ApiErrorResponse errorResponse) {
        return "Error: " + errorResponse.getErrorCode().name() + ", message: " + errorResponse.getMessage();
    }
}
