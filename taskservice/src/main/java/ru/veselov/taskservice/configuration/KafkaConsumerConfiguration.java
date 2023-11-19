package ru.veselov.taskservice.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerConfiguration {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public Map<String,Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.REMOVE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.TYPE_MAPPINGS,
                "ru.veselov.passportprocessing.dto.GeneratePassportsDto" +
                        ":ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "ru.veselov.transducersmanagingservice.dto");
        log.debug("Configuring consumer properties");
        return props;
    }

}
