package com.krylov.scrumboard.config;

import com.krylov.scrumboard.entity.Sprint;
import com.krylov.scrumboard.service.bean.SprintConfigurer;
import com.krylov.scrumboard.service.helper.Duration;
import com.krylov.scrumboard.service.helper.LocalDateTimeConverter;
import com.krylov.scrumboard.service.helper.SprintProperties;
import com.krylov.scrumboard.service.helper.SprintServiceProps;
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

    @Bean
    public SprintConfigurer sprintConfigurer() {
        LocalDateTimeConverter converter = new LocalDateTimeConverter();
        SprintConfigurer configurer = new SprintConfigurer();
        configurer.setConverter(converter);
        return configurer;
    }

    @Bean
    public SprintServiceProps props() {
        return new SprintServiceProps(
                new Sprint(),
                new Sprint(),
                Duration.WEEK,
                new SprintProperties()
        );
    }
}
