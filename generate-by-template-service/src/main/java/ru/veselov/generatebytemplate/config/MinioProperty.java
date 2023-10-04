package ru.veselov.generatebytemplate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperty {

    private String url;

    private String accessKey;

    private String secretKey;

    private String bucketName;

}
