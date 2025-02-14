package com.example.facticle.common.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Slf4j
@Component
public class TimeZoneConfig {

    @PostConstruct
    public void setTimeZone(){
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul")); //시간을 UTC가 아니라 한국 시간대로 설정
        log.info("✅ TimeZone Set to Asia/Seoul");
    }
}
