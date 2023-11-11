package ru.veselov.taskservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.veselov.taskservice.dto.GeneratePassportsDto;
import ru.veselov.taskservice.dto.SerialNumberDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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

    public static List<SerialNumberDto> SERIALS_DTOS = List.of(SERIAL_DTO_1, SERIAL_DTO_2, SERIAL_DTO_3);


    public static GeneratePassportsDto getGeneratePassportsDto() {
        return new GeneratePassportsDto(SERIALS_DTOS, TEMPLATE_ID_STR, PRINT_DATE);
    }

    public static String jsonStringFromGeneratePassportsDto(GeneratePassportsDto generatePassportsDto) throws JsonProcessingException {
        ObjectMapper objectMapper = JsonMapper.builder().addModule(new JavaTimeModule()).build();
        return objectMapper.writeValueAsString(generatePassportsDto);
    }


    private TestUtils() {
    }
}
