package ru.veselov.taskservice.configuration.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerErrorHandler implements CommonErrorHandler {

    @Override
    public boolean handleOne(@NonNull Exception thrownException,
                             @NonNull ConsumerRecord<?, ?> consumerRecord,
                             @NonNull Consumer<?, ?> consumer,
                             @NonNull MessageListenerContainer container) {
        log.error("Error caused in kafka consumer service with details: "
                        + "[exception: {}, record: {}, consumer: {}, container: {}",
                thrownException.getMessage(), consumerRecord, consumer.groupMetadata(), container.getGroupId());
        return true;
    }

    @Override
    public void handleOtherException(Exception thrownException,
                                     Consumer<?, ?> consumer,
                                     MessageListenerContainer container,
                                     boolean batchListener) {
        log.error("Error caused in kafka consumer service with details: "
                        + "[exception: {}, consumer: {}, container: {}",
                thrownException.getMessage(), consumer.groupMetadata(), container.getGroupId());
        log.error("Seek to end command will be performed for [partitions: {}", container.getAssignedPartitions());
        consumer.seekToEnd(container.getAssignedPartitions());
    }

}
