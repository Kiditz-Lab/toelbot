package com.toelbox.chatbot;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableBatchProcessing
@EnableFeignClients
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
