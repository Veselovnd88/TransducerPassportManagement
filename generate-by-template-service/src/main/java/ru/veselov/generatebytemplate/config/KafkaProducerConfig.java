package ru.veselov.generatebytemplate.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaProducerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, true);
        log.trace("Creating properties for Kafka Producer Configuration");
        return props;
    }

    @Bean
    @Qualifier("passport-producer")
    public ProducerFactory<String, GeneratePassportsDto> passportProducerFactory() {
        log.info("Creating producer factory for GeneratePassportsDto");
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, GeneratePassportsDto> passportKafkaTemplate(
            @Qualifier("passport-producer") ProducerFactory<String, GeneratePassportsDto> producerFactory) {
        KafkaTemplate<String, GeneratePassportsDto> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setObservationEnabled(true);
        log.info("Creating Kafka Template for GeneratePassportDto");
        return kafkaTemplate;
    }

    @Bean
    @Qualifier("task-producer")
    public ProducerFactory<String, TaskResultDto> taskProducerFactory() {
        log.info("Creating producer factory for TaskResultDto");
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, TaskResultDto> taskKafkaTemplate(
            @Qualifier("task-producer") ProducerFactory<String, TaskResultDto> producerFactory) {
        KafkaTemplate<String, TaskResultDto> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setObservationEnabled(true);
        log.info("Creating Kafka Template for TaskResultDto");
        return kafkaTemplate;
    }

}
