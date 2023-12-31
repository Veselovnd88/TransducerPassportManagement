package ru.veselov.taskservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.mockito.Mockito;
import reactor.core.publisher.FluxSink;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.dto.SerialNumberDto;
import ru.veselov.taskservice.entity.TaskStatus;
import ru.veselov.taskservice.events.StatusStreamMessage;
import ru.veselov.taskservice.events.SubscriptionData;
import ru.veselov.taskservice.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class TestUtils {

    public static final String USERNAME = "username";

    public static final String SERIAL = "192030A4050";

    public static final UUID TEMPLATE_ID = UUID.randomUUID();

    public static final String TEMPLATE_ID_STR = TEMPLATE_ID.toString();

    public static final LocalDate PRINT_DATE = LocalDate.now();

    public static final UUID SERIAL_ID = UUID.randomUUID();
    public static final UUID TASK_ID = UUID.randomUUID();

    public static final SerialNumberDto SERIAL_DTO_1 = new SerialNumberDto(UUID.randomUUID().toString(), "1");

    public static final SerialNumberDto SERIAL_DTO_2 = new SerialNumberDto(UUID.randomUUID().toString(), "2");

    public static final SerialNumberDto SERIAL_DTO_3 = new SerialNumberDto(UUID.randomUUID().toString(), "3");

    public static final String TASK_ID_STR = TASK_ID.toString();

    public static final UUID SUB_ID = UUID.randomUUID();

    public static final UUID FILE_ID = UUID.randomUUID();

    public static final String FILE_ID_STR = FILE_ID.toString();

    public static final String JSON_ERROR_CODE = "$.errorCode";

    public static final String JSON_VIOLATIONS_FIELD = "$.violations[0].fieldName";

    public static List<SerialNumberDto> SERIALS_DTOS = List.of(SERIAL_DTO_1, SERIAL_DTO_2, SERIAL_DTO_3);

    public static GeneratePassportsDto getGeneratePassportsDto() {
        return new GeneratePassportsDto(SERIALS_DTOS, TEMPLATE_ID_STR, PRINT_DATE);
    }

    public static String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        return objectMapper.writeValueAsString(object);
    }

    public static Task getTask() {
        return new Task(TestUtils.TASK_ID,
                TaskStatus.CREATED, TestUtils.PRINT_DATE, FILE_ID_STR, LocalDateTime.now(), LocalDateTime.now());
    }

    public static SubscriptionData getSubscriptionData() {
        return new SubscriptionData(SUB_ID, TASK_ID_STR, Mockito.mock(FluxSink.class));
    }

    public static StatusStreamMessage getStatusStreamMessage() {
        StatusStreamMessage statusStreamMessage = new StatusStreamMessage();
        statusStreamMessage.setMessage("message");
        statusStreamMessage.setTask(getTask());
        statusStreamMessage.setTaskId(TASK_ID_STR);
        return statusStreamMessage;
    }

    private TestUtils() {
    }
}
