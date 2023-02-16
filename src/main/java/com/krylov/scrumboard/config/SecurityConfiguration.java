package com.krylov.scrumboard.config;

import com.krylov.scrumboard.security.filter.MyAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final AuthenticationConfiguration authConfig;

    @Value("${secret.key.for.jwt}")
    private String secretKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/v1", "/api/v1/about", "/api/v1/authenticate", "/api/v1/auth/**",
//                        "/css/**", "/js/**",
//                        "/images/**").permitAll()
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider)

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .formLogin(login -> login
//                                .loginPage("/api/v1/authenticate")
//                                .permitAll()
//                                .defaultSuccessUrl("/api/v1", true)
//                                .failureUrl("/api/v1/authenticate?error=true")
//                )
                .logout(logout -> logout
                        .logoutUrl("/api/v1/logout")
                        .deleteCookies("JSESSIONID")
                )
                .addFilter(new MyAuthenticationFilter(authenticationManager(), secretKey));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
