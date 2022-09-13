package com.testaarosa.springRecallBookApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringRecallBookApp {

    public static void main(String[] args) {
        SpringApplication.run(SpringRecallBookApp.class, args);
    }

}
