package ru.veselov.generatebytemplate.service;

import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.TaskResultDto;

public interface KafkaBrokerSender {

    void sendResultMessage(String taskId, TaskResultDto taskResultDto);

    void sendPassportInfoMessage(GeneratePassportsDto generatePassportsDto);

}
