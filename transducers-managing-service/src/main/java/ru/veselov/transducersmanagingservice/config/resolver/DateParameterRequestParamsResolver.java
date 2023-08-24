package ru.veselov.transducersmanagingservice.config.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import ru.veselov.transducersmanagingservice.annotation.DateParam;
import ru.veselov.transducersmanagingservice.dto.DateParams;

import java.time.LocalDate;

public class DateParameterRequestParamsResolver extends RequestParamMethodArgumentResolver {

    private static final String AFTER = "after";

    private static final String BEFORE = "before";

    public DateParameterRequestParamsResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DateParam.class);
    }

    @Override
    protected Object resolveName(@NonNull String name,
                                 @NonNull MethodParameter parameter,
                                 @NonNull NativeWebRequest request) {
        return new DateParams(resolveAfter(request), resolveBefore(request));
    }

    private LocalDate resolveAfter(NativeWebRequest request) {
        String after = request.getParameter(AFTER);
        if (after == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(after);
    }

    private LocalDate resolveBefore(NativeWebRequest request) {
        String before = request.getParameter(BEFORE);
        if (before == null) {
            return LocalDate.now();
        }
        return LocalDate.parse(before);
    }

}
