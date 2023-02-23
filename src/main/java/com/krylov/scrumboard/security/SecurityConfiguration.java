package com.krylov.scrumboard.security;

import com.krylov.scrumboard.security.filter.JWTAuthenticationFilter;
import com.krylov.scrumboard.security.filter.JWTAuthorizationFilter;
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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.*;


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
        JWTAuthenticationFilter authFilter = new JWTAuthenticationFilter(authenticationManager(), secretKey);
        authFilter.setFilterProcessesUrl("/api/v1/auth/login");

        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1", "/api/v1/about", "/api/v1/auth/**", "/api/v1/auth/**",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(
                                "/api/v1/team/member",
                                "/api/v1/team/manager",
                                "/api/v1/team/project").hasAuthority("ROLE_TEAM_MANAGER")
                        .requestMatchers(DELETE, "/api/v1/user/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(POST, "/api/v1/user/role").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider)

                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .formLogin(login -> login
//                                .loginPage("/api/v1/auth/login")
//                                .permitAll()
//                                .defaultSuccessUrl("/api/v1", true)
//                                .failureUrl("/api/v1/auth/login?error=true")
//                )
//                .logout(logout -> logout
//                        .logoutUrl("/api/v1/auth/logout")
//                        .deleteCookies("JSESSIONID")
//                )
                .addFilter(authFilter)
                .addFilterBefore(new JWTAuthorizationFilter(secretKey), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
