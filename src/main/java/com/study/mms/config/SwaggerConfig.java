package com.study.mms.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	// 스웨거 주소 (로컬)
	// http://localhost:8200/swagger-ui.html

	@Bean
	GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder().group("public-api").pathsToMatch("/api/**", "/user/**", "/admin/**").build();
	}

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components().addSecuritySchemes("basicAuth",
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
				.addSecurityItem(new SecurityRequirement().addList("basicAuth"))
				.info(new Info().title("Study Group API").version("1.0")
						.description("API documentation for Study Group application"));
	}

//	@Bean
//	public OpenAPI customOpenAPI() {
//		// Basic Authentication 설정
//		SecurityScheme basicAuthScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"); 
//
//		SecurityRequirement securityItem = new SecurityRequirement().addList("basicAuth");
//
//		// OpenAPI 설정에 보안 스키마와 인증 요구 사항 추가
//		return new OpenAPI().components(new Components().addSecuritySchemes("basicAuth", basicAuthScheme))
//				.addSecurityItem(securityItem).info(new Info().title("API Documentation").version("1.0.0"));
//	}

}
