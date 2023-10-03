package ru.veselov.generatebytemplate.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.passport-topic}")
    private String topic;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    //added for understanding, who add topics, not necessary
    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic createdPassportTopic() {
        return TopicBuilder.name(topic)
                .partitions(2)
                //messages would be deleted after 5 days
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(Duration.ofDays(5).toMillis()))
                .build();
    }

}
