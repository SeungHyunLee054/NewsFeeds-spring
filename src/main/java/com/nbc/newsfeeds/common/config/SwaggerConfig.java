package com.nbc.newsfeeds.common.config;

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
	public OpenAPI openApi() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("bearer-key"))
			.components(new Components()
				.addSecuritySchemes("bearer-key",
					new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("News Feeds API")
			.description("News-Feeds 팀 프로젝트 과제")
			.version("1.0");
	}
}


