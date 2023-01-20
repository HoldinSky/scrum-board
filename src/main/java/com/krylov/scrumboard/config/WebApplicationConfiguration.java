package com.krylov.scrumboard.config;

import com.krylov.scrumboard.service.bean.SprintConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class WebApplicationConfiguration {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.httpBasic().disable();
//        return http.build();
//    }

    @Bean
    public SprintConfigurer sprintConfiguration() {
        return new SprintConfigurer();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers("api/v1");
    }
}
