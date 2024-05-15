package com.example.dreamvalutbackend.config.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Value("${domain.backend}")
	private String backendUrl;

	@Bean
	public OpenAPI openAPI() {
		String jwt = "JWT";
		SecurityScheme securityScheme = new SecurityScheme()
			.name("Authorization")
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT");

		Components components = new Components()
			.addSecuritySchemes(jwt, securityScheme);

		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);

		return new OpenAPI()
			.components(components)
			.info(apiInfo())
			.addSecurityItem(securityRequirement)
			.addServersItem(new Server().url("http://localhost:8080"))
			.addServersItem(new Server().url(backendUrl));
	}

	private Info apiInfo() {
		return new Info()
			.title("DreamVault")
			.description("DreamVault API")
			.version("1.0.0");
	}

	@Bean
	public ModelResolver modelResolver(ObjectMapper objectMapper) {
		return new ModelResolver(objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE));
	}
}
