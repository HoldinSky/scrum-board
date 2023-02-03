package com.krylov.scrumboard.config;

import com.krylov.scrumboard.service.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers(
                        "/api/v1", "/api/v1/more", "/api/v1/about", "/api/v1/authenticate", "/api/v1/auth/**",
                        "/css/**", "/js/**",
                        "/images/**").permitAll()
                .anyRequest().authenticated()
//                .formLogin()
//                .loginPage("/api/v1/authenticate")
//                .loginProcessingUrl("/api/v1/auth/login")
//                .defaultSuccessUrl("/api/v1", true)
//                .failureUrl("/api/v1/
//                .and()login?error=true")
                .and()
                .logout()
                .logoutUrl("/api/v1/logout")
                .deleteCookies("JSESSIONID")
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .invalidSessionUrl("/api/v1/invalidSession")
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



}
