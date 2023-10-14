package ru.veselov.generatebytemplate.app;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.controller.TemplateController;
import ru.veselov.generatebytemplate.dto.TemplateDto;
import ru.veselov.generatebytemplate.exception.error.ErrorCode;
import ru.veselov.generatebytemplate.service.PassportTemplateService;

@WebMvcTest(controllers = TemplateController.class)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class TemplateControllerValidationIntegrationTest {

    public static final String URL_PREFIX = "/api/v1/template/";

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    PassportTemplateService passportTemplateService;

    @Test
    void shouldReturnValidationErrorForUUIDFieldWhenGetTemplate() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/source").path("/id/" + "not_UUID")
                        .build())
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnValidationErrorNoDocxFileForUpload() {
        //Checking @Docx annotation for validation
        TemplateDto templateDto = new TemplateDto(
                "filename",
                TestUtils.TEMPLATE_ID.toString(),
                "bucketName");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES).filename("filename.pox");
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("file");
    }

    @Test
    void shouldReturnValidationErrorForWrongTemplateFieldsForUpload() {
        TemplateDto templateDto = new TemplateDto(null, null, null);
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/upload").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")))
                .jsonPath("$.violations[1].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")))
                .jsonPath("$.violations[2].fieldName")
                .value(Matchers.anyOf(Matchers.containsString("templateDescription"),
                        Matchers.containsString("ptArt"), Matchers.containsString("bucket")));
    }

    @Test
    void shouldReturnValidationErrorNoDocxFileForUpdate() {
        //Checking @Docx annotation for validation
        TemplateDto templateDto = new TemplateDto(
                "filename",
                TestUtils.TEMPLATE_ID.toString(),
                "bucketName");
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES).filename("filename.pox");
        multipartBodyBuilder.part(TestUtils.MULTIPART_DTO, templateDto);

        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + TestUtils.TEMPLATE_ID).build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("file");
    }

    @Test
    void shouldReturnValidationErrorForUUIDFieldForUpdate() {
        MultipartBodyBuilder multipartBodyBuilder = new MultipartBodyBuilder();
        multipartBodyBuilder.part(TestUtils.MULTIPART_FILE, TestUtils.SOURCE_BYTES)
                .filename(TestUtils.MULTIPART_FILENAME);
        webTestClient.put().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/update/upload")
                        .path("/id/" + "not UUID").build())
                .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

    @Test
    void shouldReturnValidationErrorForUUIDFieldForDelete() {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(URL_PREFIX).path("/delete")
                        .path("/id/" + "not UUID").build())
                .exchange().expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody().jsonPath("$.errorCode").isEqualTo(ErrorCode.ERROR_VALIDATION.toString())
                .jsonPath("$.violations[0].fieldName").isEqualTo("templateId");
    }

}
