package com.kargobaji.kargobaji.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //  설정 클래스로 정의
@EnableWebSecurity // Security 설정을 활성화
@EnableMethodSecurity // 메서드 보안 설정 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보안 설정 off
                .authorizeHttpRequests( // HTTP 요청에 대한 인가 설정
                        authorize -> authorize
                                .requestMatchers("/open-api").permitAll()
                                .requestMatchers("/open-api/sync").permitAll()
                                .requestMatchers("/open-csv").permitAll()
                                .anyRequest().authenticated() // 나머지 모든 요청은 인증된 사용자만 접근 가능.
                        );
        return http.build();
    }
}
