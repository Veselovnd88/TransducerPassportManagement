package ru.veselov.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
        SecurityErrorHelper.addContentTypeHeaderToResponse(exchange);
        ApiErrorResponse errorResponse = createErrorResponse(exception);
        SecurityErrorHelper.addWWWAuthenticationHeaderWithErrorInformation(exchange, errorResponse);
        byte[] messageBytes = SecurityErrorHelper.createJsonErrorMessage(errorResponse);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(messageBytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

    private static ApiErrorResponse createErrorResponse(AuthenticationException exception) {
        if (exception instanceof InvalidBearerTokenException && exception.getMessage().startsWith("Jwt expired")) {
            return new ApiErrorResponse(ErrorCode.JWT_EXPIRED, exception.getMessage());
        }
        if (exception instanceof AuthenticationCredentialsNotFoundException) {
            return new ApiErrorResponse(ErrorCode.UNAUTHENTICATED, "Token not found in header");
        }
        return new ApiErrorResponse(ErrorCode.UNAUTHENTICATED, exception.getMessage());
    }

}
