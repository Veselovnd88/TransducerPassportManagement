package ru.veselov.passportprocessing.service.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class PdfServiceImplTest {

    public static WireMockServer wireMockServer;

    public static int PORT = 30001;

    public PdfServiceImpl pdfService;

    private String pdfConverterUrl;

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterAll
    @SneakyThrows
    static void shutdown() {
        wireMockServer.shutdown();
    }

    @BeforeEach
    void init() {
        WebClient webClient = WebClient.create();
        pdfConverterUrl = "http://localhost:%d".formatted(PORT);
        pdfService = new PdfServiceImpl(webClient);
        ReflectionTestUtils.setField(pdfService, "pdfConverterUrl", pdfConverterUrl, String.class);
    }

    @Test
    @SneakyThrows
    void shouldCallPdfService() {
        WireMock.configureFor("localhost", PORT);
        WireMock.stubFor(WireMock.post("/")
                .willReturn(WireMock.aResponse().withStatus(200).withBody(new byte[]{1, 2, 3, 4})));
        pdfService.createPdf(new byte[]{1, 2, 3, 4, 5});
        WireMock.verify(WireMock.postRequestedFor(WireMock.urlEqualTo("/")));
    }

}