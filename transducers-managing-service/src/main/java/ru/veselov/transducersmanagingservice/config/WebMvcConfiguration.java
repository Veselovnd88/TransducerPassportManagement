package ru.veselov.transducersmanagingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.veselov.transducersmanagingservice.config.resolver.DateParameterRequestParamsResolver;
import ru.veselov.transducersmanagingservice.config.resolver.SortParameterRequestParamsResolver;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(@NonNull List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(new SortParameterRequestParamsResolver(true));
        resolvers.add(new DateParameterRequestParamsResolver(true));
    }

}
