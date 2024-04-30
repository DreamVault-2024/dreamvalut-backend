package com.example.dreamvalutbackend.config.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
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
			.addSecurityItem(securityRequirement);
	}

	private Info apiInfo() {
		return new Info()
			.title("DreamVault")
			.description("DreamVault API")
			.version("1.0.0");
	}
}
