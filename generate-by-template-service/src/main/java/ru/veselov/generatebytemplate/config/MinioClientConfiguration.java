package ru.veselov.generatebytemplate.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketLifecycleArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.messages.Expiration;
import io.minio.messages.LifecycleConfiguration;
import io.minio.messages.LifecycleRule;
import io.minio.messages.ResponseDate;
import io.minio.messages.RuleFilter;
import io.minio.messages.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import ru.veselov.generatebytemplate.exception.MinioBucketAdjustingException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MinioClientConfiguration {

    @Value("${minio.buckets.result}")
    private String resultBucket;

    @Value("${scheduling.days-until-delete-result}")
    private int daysUntilDeleteResult;

    private final MinioProperty minioProperty;

    @Bean
    public MinioClient minioClient() {
        log.trace("Creating MinIO client");
        return MinioClient.builder().endpoint(minioProperty.getUrl())
                .credentials(minioProperty.getAccessKey(), minioProperty.getSecretKey())
                .build();
    }

    @EventListener(ContextRefreshedEvent.class)
    public void createAndConfigureBuckets() {
        minioProperty.getBuckets().forEach((prop, bucket) -> {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder().bucket(bucket).build();
            try {
                boolean isBucketCreated = minioClient().bucketExists(bucketExistsArgs);
                if (!isBucketCreated) {
                    MakeBucketArgs makeBucket = MakeBucketArgs.builder().bucket(bucket).build();
                    minioClient().makeBucket(makeBucket);
                    if (bucket.equals(resultBucket)) {
                        minioClient().setBucketLifecycle(createLifeCycleObj(resultBucket));
                        log.info("Adjusting files lifecycle for bucket: {}", bucket);
                    }
                    log.info("[Bucket: {}] created", bucket);
                }
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                log.error("Error during MinIO client constructing caused [{}]", e.getMessage());
                throw new MinioBucketAdjustingException(e.getMessage(), e);
            }
        });
    }

    private SetBucketLifecycleArgs createLifeCycleObj(String bucket) {
        Expiration expiration = new Expiration((ResponseDate) null, daysUntilDeleteResult + 3, null);
        LifecycleRule lifecycleRule = new LifecycleRule(Status.ENABLED, null,
                expiration, new RuleFilter("delete"), null, null, null, null);
        LifecycleConfiguration lifecycleConfiguration = new LifecycleConfiguration(List.of(lifecycleRule));
        return SetBucketLifecycleArgs.builder().bucket(bucket).config(lifecycleConfiguration).build();
    }

}
