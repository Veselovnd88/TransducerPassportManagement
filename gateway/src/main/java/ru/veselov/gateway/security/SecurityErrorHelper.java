package ru.veselov.gateway.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import ru.veselov.gateway.exception.ApiErrorResponse;

public class SecurityErrorHelper {

    private SecurityErrorHelper() {
    }

    public static void addContentTypeHeaderToResponse(ServerWebExchange exchange) {
        exchange.getResponse().getHeaders().add(SecurityConstant.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    public static void addWWWAuthenticationHeaderWithErrorInformation(ServerWebExchange exchange,
                                                                      ApiErrorResponse response) {
        exchange.getResponse().getHeaders().add(SecurityConstant.WWW_AUTH_HEADER,
                computeWWWAuthenticateMessage(response));
    }

    public static byte[] createJsonErrorMessage(ApiErrorResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsBytes(response);
        } catch (JsonProcessingException e) {
            return "Can't create correct error message for unauthenticated error".getBytes();
        }
    }

    private static String computeWWWAuthenticateMessage(ApiErrorResponse errorResponse) {
        return "Error: " + errorResponse.getErrorCode().name() + ", message: " + errorResponse.getMessage();
    }
}
