package ru.veselov.generatebytemplate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.TemplateStorageService;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class TemplateInfoControllerTest {

    private final static String URL = "/api/v1/template/info";

    private final static String templateId = TestUtils.TEMPLATE_ID.toString();

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
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/id/" + templateId).build())
                .exchange().expectStatus().isOk().expectBody(Template.class);
    }

    @Test
    void shouldReturnTemplatesWithSortingParams() {
        List<Template> templates = List.of(new Template());
        Mockito.when(templateStorageService.findAll(Mockito.any())).thenReturn(templates);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/all").queryParam(TestUtils.PAGE, 1).build())
                .exchange().expectStatus().isOk()
                .expectBody(List.class);
    }

    @Test
    void shouldReturnTemplatesByPtArtWithSortingParams() {
        List<Template> templates = List.of(new Template());
        Mockito.when(templateStorageService.findAllByPtArt(Mockito.anyString(), Mockito.any())).thenReturn(templates);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/all/ptArt/801877")
                        .queryParam(TestUtils.PAGE, 1).build())
                .exchange().expectStatus().isOk()
                .expectBody(List.class);
    }

}
