package com.test.teamlog.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_KEY = "bearer-jwt";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(getComponents())
                .info(getApiInfo())
                .addSecurityItem(getSecurityItem());
    }

    private static SecurityRequirement getSecurityItem() {
        return new SecurityRequirement().addList(SECURITY_SCHEME_KEY);
    }

    private Components getComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_KEY,
                        new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .in(SecurityScheme.In.HEADER)
                                .scheme("bearer")
                                .bearerFormat("JWT"));
    }

    private Info getApiInfo() {
        return new Info()
                .title("TeamLog API 명세")
                .version("v0.0.1")
                .description("TeamLog API 명세");

    }
}