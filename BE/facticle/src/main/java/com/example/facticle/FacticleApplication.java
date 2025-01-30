package com.example.facticle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class) //인증단계 구현 전이므로 spring security 비활성화
public class FacticleApplication {


	public static void main(String[] args) {
		SpringApplication.run(FacticleApplication.class, args);
	}

}
