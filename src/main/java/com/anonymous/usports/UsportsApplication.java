package com.anonymous.usports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UsportsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsportsApplication.class, args);
    }

}
