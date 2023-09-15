package ru.veselov.passportprocessing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import java.time.Duration;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.passport-topic}")
    private String topic;

    @Bean
    public NewTopic createdPassportTopic() {
        return TopicBuilder.name(topic)
                .partitions(1)
                .replicas(1)
                //messages would be deleted after 5 days
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofDays(5).toMillis()))
                .build();
    }

}
