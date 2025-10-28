package com.carpoor.alchabackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class AlchaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlchaBackendApplication.class, args);
    }

}
