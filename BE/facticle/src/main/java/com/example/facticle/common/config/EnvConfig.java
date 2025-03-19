package com.example.facticle.common.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * 애플리케이션 시작 시 .env 파일을 자동으로 로드
 */
@Slf4j
@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir")) // 프로젝트 루트에서 .env 찾기
                .ignoreIfMissing() // .env 파일이 없어도 예외 발생 안 함
                .load();

        // 환경 변수 로드
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
