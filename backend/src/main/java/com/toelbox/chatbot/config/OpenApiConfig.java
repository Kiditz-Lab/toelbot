package com.toelbox.chatbot.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
class OpenApiConfig {

	//	private static final String API_KEY = "apiKey";
	private static final String BEARER_AUTH = "bearer";

	@Bean
	public OpenAPI customOpenAPI() {
		Server server1 = new Server();
		server1.setUrl("https://dev.toelbox.com");
		server1.setDescription("Development Server");

		Server server2 = new Server();
		server2.setUrl("http://localhost:8080");
		server2.setDescription("Local Server");

		return new OpenAPI()
				.servers(Arrays.asList(server2, server1))
				.components(new Components()
								.addSecuritySchemes(BEARER_AUTH,
										new SecurityScheme()
												.type(SecurityScheme.Type.HTTP)
												.scheme("bearer")
												.bearerFormat("JWT"))
//						.addSecuritySchemes(API_KEY,
//								new SecurityScheme()
//										.type(SecurityScheme.Type.APIKEY)
//										.in(SecurityScheme.In.HEADER)
//										.name("X-API-KEY"))
				)
				.info(new Info()
						.description("Toelbox API")
						.termsOfService("http://toelbox.com/terms/")
						.license(new License()
								.name("Apache 2.0")
								.url("http://springdoc.org")))
				.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
	}
}
