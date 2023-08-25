package ru.veselov.transducersmanagingservice;

import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;

import java.time.LocalDate;
import java.util.UUID;

public class TestConstants {

    public static final UUID SERIAL_ID = UUID.randomUUID();

    public static final UUID CUSTOMER_ID = UUID.randomUUID();

    public static final String PT_ART = "801877";

    public static final LocalDate AFTER = LocalDate.parse("2023-08-01");

    public static final LocalDate BEFORE = LocalDate.parse("2023-08-25");

    public static final SortingParams SORTING_PARAMS = new SortingParams(0, "ptArt", "asc");

    public static final DateParams DATE_PARAMS = new DateParams(AFTER, BEFORE);
}
