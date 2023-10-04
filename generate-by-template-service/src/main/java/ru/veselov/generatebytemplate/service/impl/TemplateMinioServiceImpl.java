package ru.veselov.generatebytemplate.service.impl;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
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
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.model.Template;
import ru.veselov.generatebytemplate.service.TemplateMinioService;

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
    public ByteArrayResource getTemplateByName(String filename) {
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucketName).object(filename).build();
        try {
            GetObjectResponse object = minioClient.getObject(objectArgs);
            byte[] bytes = object.readAllBytes();
            log.info("Retrieved [file: {}] from storage", filename);
            return new ByteArrayResource(bytes);
        } catch (ErrorResponseException | InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException | InsufficientDataException |
                 InternalException e) {
            log.error("Error occurred during getting file from storage: {}", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void saveTemplate(Resource resource, Template template) {
        try {
            PutObjectArgs saveArgs = createSaveArgs(resource, template);
            minioClient.putObject(saveArgs);
        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error with minio occurred during [saving: {}]", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
        log.info("Template saved to MinIO storage: [bucket: {}, filename: {}]",
                template.getBucket(), template.getFilename());
    }

    @Override
    public void updateTemplate(Resource resource, Template template) {
        try {
            PutObjectArgs saveArgs = createSaveArgs(resource, template);
            minioClient.putObject(saveArgs);
            log.info("Template updated in MinIO storage: [bucket: {}, filename: {}]",
                    template.getBucket(), template.getFilename());
        } catch (ErrorResponseException | InsufficientDataException | InvalidResponseException | InternalException |
                 InvalidKeyException | IOException | ServerException | NoSuchAlgorithmException |
                 XmlParserException e) {
            log.error("Error with minio occurred during [updating: {}]", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteTemplate(String filename) {
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().object(filename).bucket(bucketName).build();
        try {
            minioClient.removeObject(removeObjectArgs);
            log.info("Template deleted from MinIO storage: [bucket: {}, filename: {}]", bucketName, filename);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 InvalidResponseException e) {
            log.error("Error with minio occurred during [deleting: {}]", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    private PutObjectArgs createSaveArgs(Resource resource, Template template) throws IOException {
        return PutObjectArgs.builder().bucket(template.getBucket())
                .object(template.getFilename())
                .stream(resource.getInputStream(), resource.contentLength(), 0)
                .build();
    }

}
