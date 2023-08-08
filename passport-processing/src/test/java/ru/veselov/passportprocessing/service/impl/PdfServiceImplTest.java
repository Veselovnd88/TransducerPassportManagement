package ru.veselov.passportprocessing.service.impl;

import lombok.SneakyThrows;
import mockwebserver3.MockWebServer;
import mockwebserver3.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

class PdfServiceImplTest {

    public static MockWebServer mockPdfServer;

    public PdfServiceImpl pdfService;

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        mockPdfServer = new MockWebServer();
        mockPdfServer.start();
    }

    @AfterAll
    @SneakyThrows
    static void shutdown() {
        mockPdfServer.shutdown();
    }

    @BeforeEach
    void init() {
        WebClient webClient = WebClient.create();
        String pdfConverterUrl = "http://localhost:%s".formatted(mockPdfServer.getPort());
        pdfService = new PdfServiceImpl(webClient);
        ReflectionTestUtils.setField(pdfService, "pdfConverterUrl", pdfConverterUrl, String.class);
    }

    @Test
    @SneakyThrows
    void shouldCallWebServer() {
        pdfService.createPdf(new byte[]{1, 2, 3, 4, 5});
        RecordedRequest request = mockPdfServer.takeRequest();
        Assertions.assertThat(request.getMethod()).isEqualTo("POST");
    }

}