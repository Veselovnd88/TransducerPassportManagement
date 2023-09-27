package ru.veselov.gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.veselov.gateway.exception.ApiErrorResponse;
import ru.veselov.gateway.exception.ErrorCode;

@Slf4j
public class GatewayAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException exception) {
        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
        SecurityErrorHelper.addContentTypeHeaderToResponse(exchange);
        ApiErrorResponse errorResponse = new ApiErrorResponse(ErrorCode.UNAUTHORIZED,
                "Not correct role, " + exception.getMessage());
        SecurityErrorHelper.addWWWAuthenticationHeaderWithErrorInformation(exchange, errorResponse);
        byte[] messageBytes = SecurityErrorHelper.createJsonErrorMessage(errorResponse);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(messageBytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

}
