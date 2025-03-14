package com.example.facticle.news.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsServiceTest {

    @BeforeAll
    static void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Test
    void 임시테스트(){
        System.out.println("======== 🌍 System Time Info 🌍 ========");

        // 1️⃣ 기본 시스템 시간대 확인
        System.out.println("System Default TimeZone: " + TimeZone.getDefault().getID());

        // 2️⃣ 현재 시스템 시간 (System.currentTimeMillis())
        System.out.println("System Current Time (ms): " + System.currentTimeMillis());

        // 3️⃣ 현재 LocalDateTime (시스템 기본 시간대)
        System.out.println("LocalDateTime.now(): " + LocalDateTime.now());

        // 4️⃣ 현재 ZonedDateTime (시스템 기본 시간대)
        System.out.println("ZonedDateTime.now(): " + ZonedDateTime.now());

        // 5️⃣ 현재 UTC 시간
        System.out.println("Instant.now() (UTC): " + Instant.now());

        // 6️⃣ 현재 UTC 기준 LocalDateTime
        System.out.println("LocalDateTime (UTC): " + LocalDateTime.now(ZoneId.of("UTC")));

        // 7️⃣ 현재 KST 기준 LocalDateTime
        System.out.println("LocalDateTime (KST): " + LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        // 8️⃣ 현재 ZonedDateTime (UTC)
        System.out.println("ZonedDateTime (UTC): " + ZonedDateTime.now(ZoneId.of("UTC")));

        // 9️⃣ 현재 ZonedDateTime (KST)
        System.out.println("ZonedDateTime (KST): " + ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

        // 🔟 Date 클래스로 현재 시간 출력 (이전 방식)
        System.out.println("Date (Old Style, System Default): " + new Date());

        // 🔢 시스템 환경 변수에서 시간대 가져오기
        System.out.println("System Property user.timezone: " + System.getProperty("user.timezone"));

        System.out.println("=========================================");
    }

}