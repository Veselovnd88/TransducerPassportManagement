package ru.veselov.miniotemplateservice.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import ru.veselov.miniotemplateservice.annotation.SortingParam;
import ru.veselov.miniotemplateservice.dto.SortingParams;

public class SortParameterRequestParamsResolver extends RequestParamMethodArgumentResolver {

    private static final String SORT = "sort";

    private static final String PAGE = "page";

    public static final String ORDER = "order";


    public SortParameterRequestParamsResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(SortingParam.class);
    }

    @Override
    protected Object resolveName(@NonNull String name,
                                 @NonNull MethodParameter parameter,
                                 @NonNull NativeWebRequest request) {
        return new SortingParams(
                resolvePageNumber(request),
                resolveSortingField(request),
                resolveOrder(request)
        );
    }

    private Integer resolvePageNumber(NativeWebRequest request) {
        String page = request.getParameter(PAGE);
        return page == null ? 0 : Integer.parseInt(page);
    }

    private String resolveSortingField(NativeWebRequest request) {
        String sort = request.getParameter(SORT);
        return sort == null ? "createdAt" : sort;
    }

    private String resolveOrder(NativeWebRequest request) {
        String order = request.getParameter(ORDER);
        return order == null ? "desc" : order;
    }

}
