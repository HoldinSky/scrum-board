package com.krylov.scrumboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.krylov.scrumboard"},
        exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"com.krylov.scrumboard.entity"})
public class ScrumBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrumBoardApplication.class, args);
    }

}
