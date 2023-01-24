package com.krylov.scrumboard.config;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.service.bean.SprintConfigurer;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.helper.SprintProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;


@Configuration
@EnableJpaRepositories(value = "com.krylov.scrumboard.repository")
public class WebApplicationConfiguration {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.httpBasic().disable();
//        return http.build();
//    }



    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers("api/v1");
    }

    @Bean
    public SprintConfigurer sprintConfigurer() {
        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        SprintConfigurer configurer = new SprintConfigurer();
        configurer.setConverter(converter);
        return configurer;
    }

    @Bean
    public Sprint sprint() {
        return new Sprint();
    }

    @Bean
    public Duration duration() {
        return Duration.NONE;
    }

    @Bean
    public SprintProperties sprintProperties() {
        return new SprintProperties();
    }
}
