package ru.veselov.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.veselov.gateway.dto.ApiErrorResponse;
import ru.veselov.gateway.dto.ErrorCode;

@Slf4j
public class GatewayAccessDeniedHandler implements ServerAccessDeniedHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        ObjectMapper objectMapper = new ObjectMapper();
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(ErrorCode.ERROR_UNAUTHORIZED,
                "Something went wrong during authorization: " + ex.getMessage());
        exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(403));
        byte[] messageBytes;
        try {
            messageBytes = objectMapper.writeValueAsBytes(apiErrorResponse);
        } catch (JsonProcessingException e) {
            messageBytes = "Can't create correct error message".getBytes();
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(messageBytes);
        return exchange.getResponse().writeWith(Flux.just(buffer));
    }

}
