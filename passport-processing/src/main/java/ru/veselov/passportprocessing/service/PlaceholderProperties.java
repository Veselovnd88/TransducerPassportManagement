package ru.veselov.passportprocessing.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "placeholder")
@Data
public class PlaceholderProperties {

    private String upperSerial;

    private String bottomSerial;

    private String date;
}
