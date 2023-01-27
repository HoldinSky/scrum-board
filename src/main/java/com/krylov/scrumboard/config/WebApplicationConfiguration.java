package com.krylov.scrumboard.config;

import com.krylov.scrumboard.bean.SprintConfigurer;
import com.krylov.scrumboard.helper.LocalDateTimeConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;


@Configuration
@EnableJpaRepositories(value = "com.krylov.scrumboard.repository")
public class WebApplicationConfiguration {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.httpBasic().disable();
//        return http.build();
//    }

    @Bean
    @Description("Thymeleaf template resolver building SCRUM app")
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("templates/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
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

}