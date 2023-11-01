package ru.veselov.generatebytemplate.controller;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.service.PassportTemplateService;

import java.util.UUID;


@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {

    private final static String URL = "/api/v1/template";

    public static final String TEMPLATE_DESC = "name";

    WebTestClient webTestClient;

    @Mock
    PassportTemplateService passportTemplateService;

    @InjectMocks
    TemplateController templateController;

    @Captor
    ArgumentCaptor<MultipartFile> multipartFileArgumentCaptor;

    @Captor
    ArgumentCaptor<TemplateDto> templateDtoArgumentCaptor;

    @Captor
    ArgumentCaptor<String> templateIdCaptor;

    @BeforeEach
    void init() {
        webTestClient = MockMvcWebTestClient.bindToController(templateController).build();
    }

    @Test
    void shouldCallPassportTemplateServiceToGetTemplateSource() {
        String templateId = UUID.randomUUID().toString();
        Mockito.when(passportTemplateService.getTemplate(templateId))
                .thenReturn(new ByteArrayResource(TestUtils.SOURCE_BYTES));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL).path("/source").path("/id/" + templateId).build())
                .exchange().expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectBody(byte[].class);
    }

    @Test
    @SneakyThrows
    void shouldCallPassportTemplateServiceToSaveTemplate() {
        TemplateDto templateDto = new TemplateDto(TEMPLATE_DESC, TestUtils.ART, TestUtils.TEMPLATE_BUCKET);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Mockito.verify(passportTemplateService, Mockito.times(1))
                .saveTemplate(multipartFileArgumentCaptor.capture(), templateDtoArgumentCaptor.capture());
        MultipartFile capturedMultipart = multipartFileArgumentCaptor.getValue();
        TemplateDto capturedTemplateDto = templateDtoArgumentCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(capturedMultipart.getBytes()).isEqualTo(TestUtils.SOURCE_BYTES),
                () -> Assertions.assertThat(capturedMultipart.getOriginalFilename())
                        .isEqualTo(TestUtils.MULTIPART_FILENAME),
                () -> Assertions.assertThat(capturedTemplateDto).isEqualTo(templateDto)
        );


    }

    @Test
    @SneakyThrows
    void shouldCallPassportTemplateServiceToUpdateTemplate() {
        String templateId = UUID.randomUUID().toString();
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES).filename(TestUtils.MULTIPART_FILENAME);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL).path("/update/upload").path("/id/" + templateId).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().isAccepted();

        Mockito.verify(passportTemplateService, Mockito.times(1))
                .updateTemplate(multipartFileArgumentCaptor.capture(), templateIdCaptor.capture());
        MultipartFile capturedMultipart = multipartFileArgumentCaptor.getValue();
        String capturedTemplateId = templateIdCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(capturedMultipart.getBytes()).isEqualTo(TestUtils.SOURCE_BYTES),
                () -> Assertions.assertThat(capturedMultipart.getOriginalFilename())
                        .isEqualTo(TestUtils.MULTIPART_FILENAME),
                () -> Assertions.assertThat(capturedTemplateId).isEqualTo(templateId)
        );

    }

    @Test
    void shouldCallTemplateServiceToDelete() {
        String templateId = UUID.randomUUID().toString();

        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL).path("/delete").path("/id/" + templateId).build())
                .exchange().expectStatus().isOk();

        Mockito.verify(passportTemplateService, Mockito.times(1)).deleteTemplate(templateIdCaptor.capture());
        String captured = templateIdCaptor.getValue();
        Assertions.assertThat(captured).isEqualTo(templateId);
    }

}
