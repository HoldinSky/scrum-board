package com.krylov.scrumboard;

import com.krylov.scrumboard.entity.Role;
import com.krylov.scrumboard.security.helper.RegistrationRequest;
import com.krylov.scrumboard.service.TeamService;
import com.krylov.scrumboard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;


@SpringBootApplication(scanBasePackages = {"com.krylov.scrumboard"},
        exclude = {SecurityAutoConfiguration.class})
@EntityScan(basePackages = {"com.krylov.scrumboard.entity"})
@RequiredArgsConstructor
public class ScrumBoardApplication {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final TeamService teamService;
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

            userService.saveUser(new RegistrationRequest("Nazar", "Krylov", "nkrylov2004@gmail.com", "password", "password"));
            userService.saveUser(new RegistrationRequest("Maria", "Khomenko", "kmaria@gmail.com", "password", "password"));
            userService.saveUser(new RegistrationRequest("Mykola", "Deruzhko", "mykoladr@gmail.com", "password", "password"));
            userService.saveUser(new RegistrationRequest("Robert", "Green", "grobert@gmail.com", "password", "password"));

            userService.addRoleToUser("nkrylov2004@gmail.com", "ROLE_ADMIN");
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

}
