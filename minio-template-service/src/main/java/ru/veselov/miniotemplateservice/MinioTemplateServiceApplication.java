package ru.veselov.miniotemplateservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class MinioTemplateServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MinioTemplateServiceApplication.class, args);
	}

}
