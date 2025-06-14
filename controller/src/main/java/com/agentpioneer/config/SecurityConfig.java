package com.agentpioneer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 禁用 CSRF 保护
        http.csrf(csrf -> csrf.disable())
            // 允许所有请求，不进行认证
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}