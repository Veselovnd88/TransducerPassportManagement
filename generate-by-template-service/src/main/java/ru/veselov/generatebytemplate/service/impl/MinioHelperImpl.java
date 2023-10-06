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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import ru.veselov.generatebytemplate.exception.CommonMinioException;
import ru.veselov.generatebytemplate.service.MinioHelper;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Helper(wrapper) for MinIO operations
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MinioHelperImpl implements MinioHelper {

    private final MinioClient minioClient;

    @Override
    public ByteArrayResource getByteArrayResource(GetObjectArgs getObjectArgs) {
        try {
            GetObjectResponse object = minioClient.getObject(getObjectArgs);
            byte[] bytes = object.readAllBytes();
            log.info("Retrieved [file: {}] from storage", getObjectArgs.object());
            return new ByteArrayResource(bytes);
        } catch (ErrorResponseException | InvalidKeyException | InvalidResponseException | IOException |
                 NoSuchAlgorithmException | ServerException | XmlParserException | InsufficientDataException |
                 InternalException e) {
            log.error("Error occurred during getting file from storage: {}", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void putObject(PutObjectArgs putObjectArgs) {
        try {
            minioClient.putObject(putObjectArgs);
            log.info("Template updated in MinIO storage: [bucket: {}, filename: {}]",
                    putObjectArgs.bucket(), putObjectArgs.object());
        } catch (ErrorResponseException | InsufficientDataException | InvalidResponseException | InternalException |
                 InvalidKeyException | ServerException | NoSuchAlgorithmException | XmlParserException |
                 IOException e) {
            log.error("Error with minio occurred during [updating: {}]", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteObject(RemoveObjectArgs removeObjectArgs) {
        try {
            minioClient.removeObject(removeObjectArgs);
            log.info("Template deleted from MinIO storage: [bucket: {}, filename: {}]",
                    removeObjectArgs.bucket(), removeObjectArgs.object());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException |
                 InvalidResponseException e) {
            log.error("Error with minio occurred during [deleting: {}]", e.getMessage());
            throw new CommonMinioException(e.getMessage(), e);
        }
    }
}
