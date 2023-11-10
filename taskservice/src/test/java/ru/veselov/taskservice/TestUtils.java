package ru.veselov.taskservice;

import java.time.LocalDate;
import java.util.UUID;

public class TestUtils {

    public static final String USERNAME = "username";

    public static final String SERIAL = "192030A4050";

    public static final UUID TEMPLATE_ID = UUID.randomUUID();

    public static final String TEMPLATE_ID_STR = TEMPLATE_ID.toString();

    public static final LocalDate PRINT_DATE = LocalDate.now();

    public static final UUID SERIAL_ID = UUID.randomUUID();


    private TestUtils() {
    }
}
