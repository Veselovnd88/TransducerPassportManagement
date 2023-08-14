package ru.veselov.miniotemplateservice.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import ru.veselov.miniotemplateservice.dto.TemplateDto;
import ru.veselov.miniotemplateservice.exception.MinioException;
import ru.veselov.miniotemplateservice.service.TemplateMinioService;
import ru.veselov.miniotemplateservice.validator.TemplateValidator;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateMinioServiceImpl implements TemplateMinioService {

    @Value("${minio.bucket-name}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public ByteArrayResource getTemplateByName(String name) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(name).build();
        try {
            GetObjectResponse object = minioClient.getObject(objectArgs);
            byte[] bytes = object.readAllBytes();
            return new ByteArrayResource(bytes);
        } catch (ErrorResponseException | InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException | InsufficientDataException |
                 InternalException e) {
            throw new MinioException(e.getMessage(), e);
        }
    }

    @Override
    public void saveTemplate(Resource resource, TemplateDto templateInfo) {
        log.info("Template saved");
    }
}
