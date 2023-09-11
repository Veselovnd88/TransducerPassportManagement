package ru.veselov.passportprocessing.service.impl;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ru.veselov.passportprocessing.exception.ServiceUnavailableException;
import ru.veselov.passportprocessing.exception.TemplateNotExistsException;

import java.util.UUID;

@WireMockTest(httpPort = 30002)
class TemplateStorageHttpClientTest {

    public static int PORT = 30002;

    public static final byte[] BYTES = new byte[]{1, 2, 3, 4};

    public static final String TEMPLATE_ID = UUID.randomUUID().toString();

    public static String urlPath;

    TemplateStorageHttpClientImpl templateStorageHttpClient;

    @BeforeEach
    void init() {
        WebClient webClient = WebClient.create();
        String templateStorageUrl = "http://localhost:%d".formatted(PORT);
        templateStorageHttpClient = new TemplateStorageHttpClientImpl(webClient);
        ReflectionTestUtils.setField(templateStorageHttpClient, "templateStorageUrl", templateStorageUrl, String.class);
        WireMock.configureFor("localhost", PORT);
        urlPath = "source/id/" + TEMPLATE_ID;
    }

    @Test
    void shouldCallTemplateStorageServiceAndReturnByteArray() {
        WireMock.stubFor(WireMock.get("/" + urlPath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value()).withBody(BYTES)));

        ByteArrayResource byteArrayResource = templateStorageHttpClient
                .sendRequestToGetTemplate(TEMPLATE_ID);

        Assertions.assertThat(byteArrayResource.getByteArray()).isEqualTo(BYTES);
    }

    @Test
    void shouldThrowNotNotFoundExceptionFor400Status() {
        WireMock.stubFor(WireMock.get("/" + urlPath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.NOT_FOUND.value())));

        Assertions.assertThatThrownBy(() ->
                        templateStorageHttpClient.sendRequestToGetTemplate(TEMPLATE_ID))
                .isInstanceOf(TemplateNotExistsException.class);
    }

    @Test
    void shouldReturnServiceUnavailableExceptionFor500Status() {
        WireMock.stubFor(WireMock.get("/" + urlPath)
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())));

        Assertions.assertThatThrownBy(() ->
                        templateStorageHttpClient.sendRequestToGetTemplate(TEMPLATE_ID))
                .isInstanceOf(ServiceUnavailableException.class);
    }

}
