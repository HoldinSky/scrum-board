package com.krylov.scrumboard.config;

import com.krylov.scrumboard.service.bean.SprintConfiguration;
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
    public SprintConfiguration sprintConfiguration() {
        return new SprintConfiguration();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers("api/v1");
    }
}
