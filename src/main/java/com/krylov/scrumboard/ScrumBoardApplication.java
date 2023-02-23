package com.krylov.scrumboard;

import com.krylov.scrumboard.entity.AppUser;
import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;


@SpringBootApplication(scanBasePackages = {"com.krylov.scrumboard"},
        exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"com.krylov.scrumboard.entity"})
@RequiredArgsConstructor
public class ScrumBoardApplication {

    private final PasswordEncoder encoder;
    public static void main(String[] args) {
        SpringApplication.run(ScrumBoardApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(UserService userService) {
        return args -> {
            userService.saveRole(new Role("ROLE_USER"));
            userService.saveRole(new Role("ROLE_ADMIN"));
            userService.saveRole(new Role("ROLE_TEAM_MEMBER"));
            userService.saveRole(new Role("ROLE_TEAM_MANAGER"));

            AppUser user1 = new AppUser("Nazar", "Krylov", "nkrylov2004@gmail.com", encoder.encode("password"));
            user1.getRoles().add(new Role("ROLE_USER"));

            AppUser user2 = new AppUser("Maria", "Khomenko", "kmaria@gmail.com", encoder.encode("password"));
            user1.getRoles().add(new Role("ROLE_USER"));

            AppUser user3 = new AppUser("Mykola", "Deruzhko", "mykoladr@gmail.com", encoder.encode("password"));
            user1.getRoles().add(new Role("ROLE_USER"));


            userService.saveUser(user1);
            userService.saveUser(user2);
            userService.saveUser(user3);

            userService.addRoleToUser("nkrylov2004@gmail.com", "ROLE_ADMIN");
        };
    }

}
