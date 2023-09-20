package ru.veselov.transducersmanagingservice;

import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.veselov.transducersmanagingservice.dto.GeneratePassportsDto;
import ru.veselov.transducersmanagingservice.dto.SerialNumberDto;

import java.util.List;
import java.util.UUID;

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

    public static GeneratePassportsDto getGeneratePassportDtoWithRandomSerials() {
        SerialNumberDto serialNumberDto = new SerialNumberDto("123", UUID.randomUUID().toString());
        SerialNumberDto serialNumberDto2 = new SerialNumberDto("456", UUID.randomUUID().toString());
        GeneratePassportsDto generatePassportsDto = Instancio.of(GeneratePassportsDto.class)
                .ignore(Select.field("serials"))
                .supply(Select.field(GeneratePassportsDto::getTemplateId), TestConstants.TEMPLATE_ID::toString)
                .create();

        generatePassportsDto.setSerials(List.of(serialNumberDto, serialNumberDto2));
        return generatePassportsDto;
    }

}
