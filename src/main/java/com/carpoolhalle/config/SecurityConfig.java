package com.carpoolhalle.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests()
                .requestMatchers( "/", "/signup", "/verify/email", "/login", "/check-email", "/verify/email", "/email-login", "/check-email-login", "login-link")
                //.requestMatchers("/login", "/check-email")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/profile/*").permitAll()
                .anyRequest().authenticated();

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){

        // static resources는 security 대상에서 제외.
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }


}
