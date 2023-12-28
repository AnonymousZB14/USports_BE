package com.anonymous.usports;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UsportsApplication {

    public static void main(String[] args) {

        SpringApplication.run(UsportsApplication.class, args);
    }

    @PostConstruct
    void timezoneSetting(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
