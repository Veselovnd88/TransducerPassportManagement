package ru.veselov.generatebytemplate.utils;

public class TestUrlConstants {

    public static final String GEN_URL = "/api/v1/generate";

    public static final String GEN_URL_TASK_ID = GEN_URL + "/" + TestUtils.TASK_ID_STR;

    public static final String TEMPLATE_URL_PREFIX = "/api/v1/template/";

    private TestUrlConstants() {

    }
}
