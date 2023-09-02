package com.testaarosa.springRecallBookApp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value(value = "${cors.origin.webui}")
    private List<String> uiCorsList;
    @Value(value = "${cors.max.age}")
    private int maxAge;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedMethods("*")
                .allowedOrigins(uiCorsList.toArray(new String[0]))
                .maxAge(maxAge)
                .allowCredentials(true);
    }
}
