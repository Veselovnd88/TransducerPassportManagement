package ru.veselov.passportprocessing.service.impl;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micrometer.observation.ObservationRegistry;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ru.veselov.passportprocessing.exception.PdfProcessingException;
import ru.veselov.passportprocessing.exception.ServiceUnavailableException;

@WireMockTest(httpPort = 30001)
class PdfHttpClientTest {

    public static int PORT = 30001;

    public PdfHttpClientImpl pdfHttpClient;

    @BeforeEach
    void init() {
        WebClient webClient = WebClient.create();
        String pdfConverterUrl = "http://localhost:%d".formatted(PORT);
        pdfHttpClient = new PdfHttpClientImpl(webClient);
        ReflectionTestUtils.setField(pdfHttpClient, "pdfConverterUrl", pdfConverterUrl, String.class);
        WireMock.configureFor("localhost", PORT);
    }

    @Test
    void shouldCallPdfService() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(200).withBody(new byte[]{1, 2, 3, 4})));
        DataBuffer dataBuffer = pdfHttpClient.sendRequestForConvertingDocxToPdf(new byte[]{1, 2, 3, 4, 5});
        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/")));
        Assertions.assertThat(dataBuffer).isNotNull();
    }

    @Test
    void shouldThrowExceptionIfStatus400() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(400)));
        Assertions.assertThatThrownBy(() -> pdfHttpClient.sendRequestForConvertingDocxToPdf(new byte[]{1, 2, 3, 4, 5}))
                .isInstanceOf(PdfProcessingException.class);
    }

    @Test
    void shouldThrowExceptionIfStatus503() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(503)));
        Assertions.assertThatThrownBy(() -> pdfHttpClient.sendRequestForConvertingDocxToPdf(new byte[]{1, 2, 3, 4, 5}))
                .isInstanceOf(ServiceUnavailableException.class);
    }

}
