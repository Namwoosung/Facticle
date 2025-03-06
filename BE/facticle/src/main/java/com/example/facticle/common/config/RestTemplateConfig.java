package com.example.facticle.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    //필요한 경우 추후 설정 추가(커넥션 풀 설정, 에러 핸들링, 로깅 등)
    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000); //연결 타임아웃 5초
        requestFactory.setReadTimeout(5000); //읽기 타임아웃 5초

        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }
}
