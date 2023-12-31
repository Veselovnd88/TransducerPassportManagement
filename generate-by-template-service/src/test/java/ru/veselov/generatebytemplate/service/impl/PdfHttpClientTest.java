package ru.veselov.generatebytemplate.service.impl;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.exception.PdfProcessingException;
import ru.veselov.generatebytemplate.exception.ServiceUnavailableException;

@WireMockTest(httpPort = 30001)
class PdfHttpClientTest {

    private static ByteArrayResource byteArrayResource;

    public static int PORT = 30001;

    public PdfHttpClientImpl pdfHttpClient;

    @BeforeEach
    void init() {
        WebClient webClient = WebClient.create();
        String pdfConverterUrl = "http://localhost:%d".formatted(PORT);
        pdfHttpClient = new PdfHttpClientImpl(webClient);
        ReflectionTestUtils.setField(pdfHttpClient, "pdfConverterUrl", pdfConverterUrl, String.class);
        WireMock.configureFor("localhost", PORT);
        byteArrayResource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
    }

    @Test
    void shouldCallPdfService() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(200).withBody(TestUtils.SOURCE_BYTES)));

        DataBuffer dataBuffer = pdfHttpClient.sendRequestForConvertingDocxToPdf(byteArrayResource);

        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/")));
        Assertions.assertThat(dataBuffer).isNotNull();
    }

    @Test
    void shouldThrowExceptionIfStatus400() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(400)));
        Assertions.assertThatThrownBy(() -> pdfHttpClient.sendRequestForConvertingDocxToPdf(byteArrayResource))
                .isInstanceOf(PdfProcessingException.class);
    }

    @Test
    void shouldThrowExceptionIfStatus503() {
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(503)));
        Assertions.assertThatThrownBy(() -> pdfHttpClient.sendRequestForConvertingDocxToPdf(byteArrayResource))
                .isInstanceOf(ServiceUnavailableException.class);
    }

}
