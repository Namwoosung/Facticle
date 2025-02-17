package com.example.facticle.news.service;

import com.example.facticle.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NaverNewsService {
    private final NewsRepository newsRepository;

    @Value("${naver.news.client-id}")
    private String clientId;

    @Value("${naver.news.client-secret}")
    private String clientSecret;

    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";

    // 테스트할 검색어 리스트 (필요 시 추가)
    private static final List<String> TEST_KEYWORDS = Arrays.asList(
            "다", "이", "뉴스", "기자", "경제", "a", "e"
    );

    @Scheduled(fixedRate = 300000) // 30초마다 실행 (테스트 목적)
    public void fetchNews() {
        RestTemplate restTemplate = new RestTemplate();

        for (String query : TEST_KEYWORDS) {
            try {
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                String apiUrl = NAVER_NEWS_API_URL + "?query=" + encodedQuery + "&display=10&sort=date";

                // API 요청 헤더 설정
                HttpHeaders headers = new HttpHeaders();
                headers.set("X-Naver-Client-Id", clientId);
                headers.set("X-Naver-Client-Secret", clientSecret);

                RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.GET, URI.create(apiUrl));
                ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);

                log.info("\n🔍 [검색어: '{}'] - 응답 코드: {}\n", query, response.getStatusCode());

                if (response.getStatusCode().is2xxSuccessful()) {
                    log.info("✅ [검색어: '{}'] - 응답 내용: {}", query, response.getBody());
                } else {
                    log.warn("❌ [검색어: '{}'] - 요청 실패: 응답 코드: {}", query, response.getStatusCode());
                }

                // 1초 대기 (API Rate Limit 보호)
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("🚨 [검색어: '{}'] - 뉴스 API 호출 중 오류 발생: {}", query, e.getMessage());
            }
        }
    }
}
