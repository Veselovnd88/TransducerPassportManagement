package ru.veselov.transducersmanagingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@SpringBootApplication
@RefreshScope
public class TransducersManagingServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransducersManagingServiceApplication.class, args);
	}

}
