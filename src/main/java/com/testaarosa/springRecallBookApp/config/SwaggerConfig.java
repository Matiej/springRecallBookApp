package com.testaarosa.springRecallBookApp.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String[] PUBLIC_PATH_TO_MACH = {"/catalog/**",
            "/uploads/**",
            "/orders/**",
            "/recipients/**",
            "/authors/**"
    };

    private static final String ADMIN_PATH_TO_MAP = "/admin/**";

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springRecallBookApp-public")
                .pathsToMatch(PUBLIC_PATH_TO_MACH)
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("springRecallBookApp-admin")
                .pathsToMatch(ADMIN_PATH_TO_MAP)
                .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BookApp API")
                        .description("Spring recall course, BookApp")
                        .version("v0.0.1")
                        .contact(new Contact().name("Maciej Wójcik").email("myEmail@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("BookApp store Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}
