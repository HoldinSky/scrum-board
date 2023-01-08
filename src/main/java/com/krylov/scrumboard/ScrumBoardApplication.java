package com.krylov.scrumboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.krylov.scrumboard"},
        exclude = {SecurityAutoConfiguration.class})
public class ScrumBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrumBoardApplication.class, args);
    }

}
