package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import ru.veselov.generatebytemplate.TestUtils;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.ResultFile;
import ru.veselov.generatebytemplate.service.MinioHelper;

import java.io.BufferedInputStream;
import java.security.InvalidKeyException;

@ExtendWith(MockitoExtension.class)
class ResultFileMinioServiceImplTest {

    @Mock
    MinioClient minioClient;

    ResultFileMinioServiceImpl generatedResultFileMinioService;

    @Captor
    ArgumentCaptor<PutObjectArgs> argumentPutObjCaptor;

    @Captor
    ArgumentCaptor<GetObjectArgs> argumentGetObjCaptor;

    @BeforeEach
    void init() {
        MinioHelper minioHelper = new MinioHelperImpl(minioClient);
        generatedResultFileMinioService = new ResultFileMinioServiceImpl(minioHelper);
        ReflectionTestUtils
                .setField(generatedResultFileMinioService, "resultBucket", TestUtils.RESULT_BUCKET, String.class);
    }

    @Test
    @SneakyThrows
    void shouldSaveResultToMinIOStorage() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        ResultFile basicResultFile = TestUtils.getBasicGeneratedResultFile();

        generatedResultFileMinioService.saveResult(resource, basicResultFile);

        Mockito.verify(minioClient).putObject(argumentPutObjCaptor.capture());
        PutObjectArgs captured = argumentPutObjCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.RESULT_BUCKET),
                () -> Assertions.assertThat(captured.object()).isEqualTo(basicResultFile.getFilename()),
                () -> {
                    try (BufferedInputStream bis = captured.stream()) {
                        Assertions.assertThat(bis.readAllBytes()).isEqualTo(resource.getContentAsByteArray());
                    }
                }
        );
    }

    @Test
    @SneakyThrows
    void shouldThrowCommonMinioException() {
        Resource resource = new ByteArrayResource(TestUtils.SOURCE_BYTES);
        ResultFile basicResultFile = TestUtils.getBasicGeneratedResultFile();
        Mockito.when(minioClient.putObject(Mockito.any())).thenThrow(InvalidKeyException.class);

        Assertions.assertThatThrownBy(
                        () -> generatedResultFileMinioService.saveResult(resource, basicResultFile))
                .isInstanceOf(CommonMinioException.class);
    }

    @Test
    @SneakyThrows
    void shouldLoadResultFileFromMinIO() {
        ResultFile basicResultFile = TestUtils.getBasicGeneratedResultFile();
        GetObjectResponse getObjectResponse = Mockito.mock(GetObjectResponse.class);
        Mockito.when(getObjectResponse.readAllBytes()).thenReturn(TestUtils.SOURCE_BYTES);
        Mockito.when(minioClient.getObject(Mockito.any())).thenReturn(getObjectResponse);

        ByteArrayResource byteArrayResource = generatedResultFileMinioService.loadResultFile(basicResultFile);

        Mockito.verify(minioClient).getObject(argumentGetObjCaptor.capture());
        GetObjectArgs captured = argumentGetObjCaptor.getValue();
        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(byteArrayResource.getByteArray()).isEqualTo(TestUtils.SOURCE_BYTES),
                () -> Assertions.assertThat(captured.object()).isEqualTo(basicResultFile.getFilename()),
                () -> Assertions.assertThat(captured.bucket()).isEqualTo(TestUtils.RESULT_BUCKET)
        );
    }

}
