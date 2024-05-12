package com.example.dreamvalutbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.dreamvalutbackend.config")
public class DreamvalutBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamvalutBackendApplication.class, args);
	}

}
