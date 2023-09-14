package ru.veselov.transducersmanagingservice.validator.impl;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.veselov.transducersmanagingservice.TestConstants;

public class TestUtils {

    public static MultiValueMap<String, String> getQueryParamsWithDateParams() {
        MultiValueMap<String, String> linkedMultiValueMap = getQuerySortingParamsOnly();
        linkedMultiValueMap.add(TestConstants.AFTER, TestConstants.DATE_AFTER.toString());
        linkedMultiValueMap.add(TestConstants.BEFORE, TestConstants.DATE_BEFORE.toString());
        return linkedMultiValueMap;
    }

    public static MultiValueMap<String, String> getQuerySortingParamsOnly() {
        LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
        linkedMultiValueMap.add(TestConstants.PAGE, TestConstants.SORTING_PARAMS.getPage().toString());
        linkedMultiValueMap.add(TestConstants.SORT, TestConstants.SORTING_PARAMS.getSort());
        linkedMultiValueMap.add(TestConstants.ORDER, TestConstants.SORTING_PARAMS.getOrder());
        return linkedMultiValueMap;
    }
}
