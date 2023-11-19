package ru.veselov.generatebytemplate.app.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class KafkaConsumerTestConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public Map<String, Object> passportConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.veselov.generatebytemplate.dto.GeneratePassportsDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.veselov.generatebytemplate.dto");
        return props;
    }


    @Bean
    public ConsumerFactory<String, GeneratePassportsDto> passportConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(passportConsumerConfig());
    }

    @Bean
    @Qualifier("passport-consumer")
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, GeneratePassportsDto>> passportListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GeneratePassportsDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(passportConsumerFactory());
        return factory;
    }

    @Bean
    public Map<String, Object> taskConsumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.veselov.generatebytemplate.dto.TaskResultDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.veselov.generatebytemplate.dto");
        return props;
    }


    @Bean
    @Qualifier("task-consumer")
    public ConsumerFactory<String, GeneratePassportsDto> taskConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(taskConsumerConfig());
    }

    @Bean
    public KafkaListenerContainerFactory<
            ConcurrentMessageListenerContainer<String, GeneratePassportsDto>> taskListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, GeneratePassportsDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskConsumerFactory());
        return factory;
    }

}
