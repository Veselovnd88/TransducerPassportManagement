package ru.veselov.generatebytemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class GenerateByTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(GenerateByTemplateApplication.class, args);
	}

}
