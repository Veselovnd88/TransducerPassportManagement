package ru.veselov.generatebytemplate;

import ru.veselov.generatebytemplate.dto.GeneratePassportsDto;
import ru.veselov.generatebytemplate.dto.SerialNumberDto;
import ru.veselov.generatebytemplate.model.ResultFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class TestUtils {

    public static final String PAGE = "page";

    public static final String SORT = "sort";

    public static final String ORDER = "order";

    public static final String SAMPLE_FILENAME = "801877-filename.docx";

    public static final String SAMPLE_TEMPLATE = "801877-filename";

    public static final String ART = "801877";

    public static final String MULTIPART_FILENAME = "filename.docx";

    public static final String MULTIPART_FILE = "file";

    public static final String MULTIPART_DTO = "template-info";

    public static final byte[] SOURCE_BYTES = new byte[]{1, 2, 3};

    public static final UUID TEMPLATE_ID = UUID.randomUUID();

    public static final String TEMPLATE_BUCKET = "templates";

    public static final String RESULT_BUCKET = "results";
    public static final String USERNAME = "username";
    public static final String TASK_ID = UUID.randomUUID().toString();
    public static final String FILE_ID = UUID.randomUUID().toString();

    public static final String TASK_TOPIC = "task";

    public static final String PASSPORT_TOPIC = "passports";

    public static List<SerialNumberDto> SERIALS_DTOS = List.of(
            new SerialNumberDto("1", UUID.randomUUID().toString()),
            new SerialNumberDto("2", UUID.randomUUID().toString()),
            new SerialNumberDto("3", UUID.randomUUID().toString()));

    public static List<String> SERIALS = List.of("1", "2", "3");

    public static final String DATE_FORMAT = "dd-MM-yyyy";

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final LocalDate DATE = LocalDate.now();

    public static GeneratePassportsDto getBasicGeneratePassportsDto() {
        return new GeneratePassportsDto(
                SERIALS_DTOS, UUID.randomUUID().toString(), DATE, TestUtils.USERNAME, TestUtils.TASK_ID);
    }

    public static ResultFile getBasicGeneratedResultFile() {
        return ResultFile.builder()
                .templateId(TEMPLATE_ID.toString())
                .taskId(TASK_ID)
                .username(USERNAME)
                .bucket(RESULT_BUCKET)
                .filename("my-filename.pdf")
                .id(UUID.randomUUID())
                .build();
    }

}
