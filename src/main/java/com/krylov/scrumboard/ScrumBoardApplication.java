package com.krylov.scrumboard;

import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import com.krylov.scrumboard.service.AuthenticationService;
import com.krylov.scrumboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import static com.krylov.scrumboard.helper.RoleNames.*;


@SpringBootApplication(scanBasePackages = {"com.krylov.scrumboard"},
        exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"com.krylov.scrumboard.entity"})
@RequiredArgsConstructor
public class ScrumBoardApplication {

    private final AuthenticationService authService;
    public static void main(String[] args) {
        SpringApplication.run(ScrumBoardApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserService userService) {
        return args -> {
            userService.saveRole(new Role(USER));
            userService.saveRole(new Role(ADMIN));
            userService.saveRole(new Role(TEAM_MEMBER));
            userService.saveRole(new Role(TEAM_MANAGER));

            authService.register(new RegistrationRequest("Nazar", "Krylov", "nkrylov2004@gmail.com", "password"));
            authService.register(new RegistrationRequest("Maria", "Khomenko", "kmaria@gmail.com", "password"));
            authService.register(new RegistrationRequest("Mykola", "Deruzhko", "mykoladr@gmail.com", "password"));

            userService.addRoleToUser("nkrylov2004@gmail.com", ADMIN);
        };
    }

}
