package ru.veselov.miniotemplateservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.miniotemplateservice.service.PassportTemplateService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    private final static String URL = "/api/v1/template";

    public static final byte[] BYTES_OUT = new byte[]{1, 2, 4};

    WebTestClient webTestClient;

    @Mock
    PassportTemplateService passportTemplateService;

    @InjectMocks
    TemplateController templateController;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(templateController).build();
    }

    @Test
    void shouldCallPassportTemplateServiceToGetTemplateSource() {
        String templateId = UUID.randomUUID().toString();
        Mockito.when(passportTemplateService.getTemplate(templateId)).thenReturn(new ByteArrayResource(BYTES_OUT));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/source").path("/" + templateId).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
    }

    @Test
    void shouldCallPassportTemplateServiceToSaveTemplate() {
        //TODO
    }

}