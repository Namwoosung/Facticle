package com.example.facticle.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer(){
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 경로에 대해
                        .allowedOrigins(
                                "http://localhost:3000",
                                "http://frontend:3000",
                                "https://localhost:3000",
                                "https://frontend:3000") // 특정 출처 허용, forntend는 docker-compose에서 front의 호스트네임
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // 허용할 HTTP 메소드 지정
                        .allowCredentials(true) // 쿠키/인증 정보 포함 여부
                        .maxAge(3600); // pre-flight request의 결과를 캐싱하는 시간(초 단위)
            }
        };
    }
}
