package com.joshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class DocumentProcessorServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocumentProcessorServiceApplication.class, args);
	}

}
