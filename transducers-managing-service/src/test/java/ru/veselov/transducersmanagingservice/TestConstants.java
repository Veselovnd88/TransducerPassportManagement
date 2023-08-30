package ru.veselov.transducersmanagingservice;

import ru.veselov.transducersmanagingservice.dto.DateParams;
import ru.veselov.transducersmanagingservice.dto.SortingParams;

import java.time.LocalDate;
import java.util.UUID;

public class TestConstants {

    public static final UUID SERIAL_ID = UUID.randomUUID();

    public static final String INN = "5167991252";

    public static final UUID CUSTOMER_ID = UUID.randomUUID();

    public static final String PT_ART = "801877";

    public static final String NUMBER = "1905201209001";

    public static final LocalDate DATE_AFTER = LocalDate.parse("2023-08-01");

    public static final LocalDate DATE_BEFORE = LocalDate.parse("2023-08-25");

    public static final SortingParams SORTING_PARAMS = new SortingParams(0, "ptArt", "asc");

    public static final DateParams DATE_PARAMS = new DateParams(DATE_AFTER, DATE_BEFORE);

    public static final String PAGE = "page";

    public static final String SORT = "sort";

    public static final String ORDER = "order";

    public static final String AFTER = "after";

    public static final String BEFORE = "before";

}
