package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.MinioHelper;

import java.io.BufferedInputStream;

@ExtendWith(MockitoExtension.class)
class TemplateMinioServiceImplTest {

    @Mock
    MinioClient minioClient;

    @InjectMocks
    TemplateMinioServiceImpl templateMinioService;

    @Captor
    ArgumentCaptor<PutObjectArgs> argumentPutObjCaptor;

    @Captor
    ArgumentCaptor<GetObjectArgs> argumentGetObjCaptor;

    @Captor
    ArgumentCaptor<RemoveObjectArgs> argumentRemoveObjCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(templateMinioService, "templateBucket", TestUtils.TEMPLATE_BUCKET, String.class);
        MinioHelper minioHelper = new MinioHelperImpl(minioClient);
        ReflectionTestUtils.setField(templateMinioService, "minioHelper", minioHelper, MinioHelper.class);
    }

    @Test
    @SneakyThrows
    void shouldPutTemplateToMinioStorage() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), TestUtils.TEMPLATE_BUCKET).create();

        templateMinioService.saveTemplate(resource, template);

        Mockito.verify(minioClient, Mockito.times(1)).putObject(argumentPutObjCaptor.capture());
        PutObjectArgs captured = argumentPutObjCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.object()).isEqualTo(template.getFilename()),
                () -> Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.TEMPLATE_BUCKET),
                () -> {
                    try (BufferedInputStream stream = captured.stream()) {
                        Assertions.assertThat(stream.readAllBytes()).isEqualTo(resource.getContentAsByteArray());
                    }
                }
        );
    }

    @Test
    @SneakyThrows
    void shouldThrowMinIOException() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), TestUtils.TEMPLATE_BUCKET).create();
        Mockito.when(minioClient.putObject(ArgumentMatchers.any())).thenThrow(ErrorResponseException.class);

        Assertions.assertThatThrownBy(() -> templateMinioService.saveTemplate(resource, template))
                .isInstanceOf(CommonMinioException.class);
    }

    @Test
    @SneakyThrows
    void shouldUpdateTemplateToMinioStorage() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        Template template = Instancio.of(Template.class)
                .ignore(Select.field(Template::getId))
                .ignore(Select.field(Template::getCreatedAt))
                .ignore(Select.field(Template::getEditedAt))
                .set(Select.field(Template::getBucket), TestUtils.TEMPLATE_BUCKET).create();

        templateMinioService.updateTemplate(resource, template);

        Mockito.verify(minioClient, Mockito.times(1)).putObject(argumentPutObjCaptor.capture());
        PutObjectArgs captured = argumentPutObjCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.object()).isEqualTo(template.getFilename()),
                () -> Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.TEMPLATE_BUCKET),
                () -> {
                    try (BufferedInputStream stream = captured.stream()) {
                        Assertions.assertThat(stream.readAllBytes()).isEqualTo(resource.getContentAsByteArray());
                    }
                }
        );
    }

    @Test
    @SneakyThrows
    void shouldGetTemplateFromMinioStorage() {
        String filename = "filename";
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(ArgumentMatchers.any())).thenReturn(getObjectResponse);

        ByteArrayResource byteArrayResource = templateMinioService.getTemplateByName(filename);

        Mockito.verify(minioClient, Mockito.times(1)).getObject(argumentGetObjCaptor.capture());
        GetObjectArgs captured = argumentGetObjCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(byteArrayResource.getByteArray()).isEqualTo(TestUtils.SOURCE_BYTES),
                () -> Assertions.assertThat(captured.object()).isEqualTo(filename),
                () -> Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.TEMPLATE_BUCKET)
        );
    }

    @Test
    @SneakyThrows
    void shouldDeleteTemplateFromMinioStorage() {
        String filename = "filename";

        templateMinioService.deleteTemplate(filename);

        Mockito.verify(minioClient, Mockito.times(1)).removeObject(argumentRemoveObjCaptor.capture());
        RemoveObjectArgs captured = argumentRemoveObjCaptor.getValue();
        Assertions.assertThat(captured.object()).isEqualTo(filename);
    }

}
