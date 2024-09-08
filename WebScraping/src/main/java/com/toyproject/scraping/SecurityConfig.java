package com.toyproject.scraping;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    http
	    	// CSRF 보호 설정
	        .csrf(csrf -> csrf
	            // CSRF 토큰을 쿠키에 저장하고, HttpOnly 속성을 해제
	            // HttpOnly 속성은 JavaScript에서 쿠키를 읽지 못하게 하지만, 
	            // withHttpOnlyFalse()를 사용하면 JS에서도 접근 가능하게 함
	            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
	        )
	        // 요청에 대한 권한 설정
	        .authorizeHttpRequests(authorize -> authorize
	            // Swagger 관련 경로들을 모두 허용 (인증 없이 접근 가능)
	            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**").permitAll()
	            // "/public/**" 경로에 대해 모든 사용자 접근을 허용 (인증 불필요)
	            .requestMatchers("/public/**").permitAll()
	            // 그 외의 모든 요청은 인증된 사용자만 접근 가능
	            .anyRequest().authenticated()
	        );

	    return http.build();
	}

}
