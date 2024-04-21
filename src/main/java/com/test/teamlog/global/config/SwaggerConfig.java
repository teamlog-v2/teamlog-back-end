package com.test.teamlog.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("TeamLog API 명세")
                .version("v0.0.1")
                .description("TeamLog API 명세");

    }

//    private OpenApiCustomiser buildSecurityOpenApi() {
//        return OpenApi -> OpenApi.addSecurityItem(new SecurityRequirement().addList("jwt token"))
//                .getComponents()
//                .addSecuritySchemes("jwt token", new SecurityScheme()
//                        .name("Authorization")
//                        .type(SecurityScheme.Type.HTTP)
//                        .in(SecurityScheme.In.HEADER)
//                        .bearerFormat("JWT")
//                        .scheme("Bearer"));
//    }
}