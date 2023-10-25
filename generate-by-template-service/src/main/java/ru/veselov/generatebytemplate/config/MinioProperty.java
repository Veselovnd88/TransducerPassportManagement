package ru.veselov.generatebytemplate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "minio")
public class MinioProperty {

    private String url;

    private String accessKey;

    private String secretKey;

    private Map<String, String> buckets;

}
