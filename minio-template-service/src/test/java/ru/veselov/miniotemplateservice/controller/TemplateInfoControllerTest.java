package ru.veselov.miniotemplateservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.miniotemplateservice.model.Template;
import ru.veselov.miniotemplateservice.service.TemplateStorageService;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class TemplateInfoControllerTest {

    private final static String URL = "/api/v1/template/info";

    private final static String templateId = UUID.randomUUID().toString();

    @Mock
    TemplateStorageService templateStorageService;

    @InjectMocks
    TemplateInfoController templateInfoController;

    WebTestClient webTestClient;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(templateInfoController).build();
    }

    @Test
    void shouldReturnTemplateById() {
        Mockito.when(templateStorageService.findTemplateById(templateId)).thenReturn(new Template());
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/" + templateId).build())
                .exchange().expectStatus().isOk().expectBody(Template.class);
    }


}