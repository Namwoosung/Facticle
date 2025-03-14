//package com.example.facticle.common.config;
//
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.util.TimeZone;
//
//@Slf4j
//@Component
//public class TimeZoneConfig {
//
//    @PostConstruct
//    public void setTimeZone(){
//        // 시스템 타임존 설정 (이미 적용되었지만, 재확인)
//        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
//        // Hibernate가 사용하는 타임존을 강제 설정
//        System.setProperty("hibernate.jdbc.time_zone", "UTC");
//
//        log.info("✅ System Default TimeZone: {}", TimeZone.getDefault().getID());
//        log.info("✅ Hibernate TimeZone Set to {}", System.getProperty("user.timezone"));
//    }
//}
