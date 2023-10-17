package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.GeneratedResultFile;
import ru.veselov.generatebytemplate.service.MinioHelper;

import java.io.BufferedInputStream;
import java.security.InvalidKeyException;

@ExtendWith(MockitoExtension.class)
class GeneratedResultFileMinioServiceImplTest {

    @Mock
    MinioClient minioClient;

    GeneratedResultFileMinioServiceImpl generatedResultFileMinioService;

    @Captor
    ArgumentCaptor<PutObjectArgs> argumentPutObjCaptor;

    @Captor
    ArgumentCaptor<GetObjectArgs> argumentGetObjCaptor;

    @Captor
    ArgumentCaptor<RemoveObjectArgs> argumentRemoveObjCaptor;

    @BeforeEach
    void init() {
        MinioHelper minioHelper = new MinioHelperImpl(minioClient);
        generatedResultFileMinioService = new GeneratedResultFileMinioServiceImpl(minioHelper);
        ReflectionTestUtils
                .setField(generatedResultFileMinioService, "resultBucket", TestUtils.RESULT_BUCKET, String.class);
    }

    @Test
    @SneakyThrows
    void shouldSaveResultToMinIOStorage() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        GeneratedResultFile basicGeneratedResultFile = TestUtils.getBasicGeneratedResultFile();
        generatedResultFileMinioService.saveResult(resource, basicGeneratedResultFile);

        Mockito.verify(minioClient, Mockito.times(1)).putObject(argumentPutObjCaptor.capture());
        PutObjectArgs captured = argumentPutObjCaptor.getValue();
        Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.RESULT_BUCKET);
        Assertions.assertThat(captured.object()).isEqualTo(basicGeneratedResultFile.getFilename());
        try (BufferedInputStream bis = captured.stream()) {
            Assertions.assertThat(bis.readAllBytes()).isEqualTo(resource.getContentAsByteArray());
        }
    }

    @Test
    @SneakyThrows
    void shouldThrowCommonMinioException() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        GeneratedResultFile basicGeneratedResultFile = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(minioClient.putObject(ArgumentMatchers.any())).thenThrow(InvalidKeyException.class);
        Assertions.assertThatThrownBy(
                        () -> generatedResultFileMinioService.saveResult(resource, basicGeneratedResultFile))
                .isInstanceOf(CommonMinioException.class);
    }

}