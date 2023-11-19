package ru.veselov.taskservice.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.veselov.taskservice.dto.TaskResultDto;

public interface KafkaListenerTaskResultService {

    void handleTaskResult(ConsumerRecord<String, TaskResultDto> record);

}
