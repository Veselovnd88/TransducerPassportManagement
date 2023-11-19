package ru.veselov.generatebytemplate.app.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.reactive.server.WebTestClientBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import ru.veselov.generatebytemplate.utils.TestUtils;
import ru.veselov.generatebytemplate.utils.AppConstants;

@TestConfiguration
public class WebTestClientTestConfiguration {

    @Bean
    public WebTestClientBuilderCustomizer webTestClientBuilderCustomizer() {
        return builder -> builder.defaultHeader(AppConstants.SERVICE_USERNAME_HEADER, TestUtils.USERNAME);
    }

}
