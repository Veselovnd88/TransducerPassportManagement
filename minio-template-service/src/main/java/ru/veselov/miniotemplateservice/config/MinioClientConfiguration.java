package ru.veselov.miniotemplateservice.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.veselov.miniotemplateservice.exception.MinioException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioClientConfiguration {

    private final MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder().endpoint(minioProperty.getUrl())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .build();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createTemplateBucket() {
        BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(minioProperty.getBucketName()).build();
        try {
            boolean isBucketCreated = minioClient().bucketExists(bucketExistsArgs);
            if (!isBucketCreated) {
                MakeBucketArgs makeBucket = MakeBucketArgs.builder().bucket(minioProperty.getBucketName()).build();
                minioClient().makeBucket(makeBucket);
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            log.error("Error during MinIO client constructing caused [{}]", e.getMessage());
            throw new MinioException(e.getMessage(), e);
        }
    }

}
