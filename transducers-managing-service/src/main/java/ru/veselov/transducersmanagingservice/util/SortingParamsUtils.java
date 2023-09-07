package ru.veselov.transducersmanagingservice.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.veselov.transducersmanagingservice.dto.SortingParams;
import ru.veselov.transducersmanagingservice.exception.PageExceedsMaximumValueException;

@UtilityClass
@Slf4j
public class SortingParamsUtils {

    public static Pageable createPageable(SortingParams sortingParams, int itemsPerPage) {
        int page = sortingParams.getPage();
        String sort = sortingParams.getSort();
        String order = sortingParams.getOrder();
        Sort sortOrder;
        if (StringUtils.equals(order, "asc")) {
            sortOrder = Sort.by(sort).ascending();
        } else {
            sortOrder = Sort.by(sort).descending();
        }
        return PageRequest.of(page, itemsPerPage).withSort(sortOrder);
    }

    public static void validatePageNumber(int page, long count, int serialsPerPage) {
        long totalPages = count / serialsPerPage;
        if (page > totalPages) {
            log.error("Page number exceeds maximum value [max: {}, was: {}}]", totalPages, page);
            throw new PageExceedsMaximumValueException("Page number exceeds maximum value [max: %s, was: %s]"
                    .formatted(totalPages, page), page);
        }
    }

}
