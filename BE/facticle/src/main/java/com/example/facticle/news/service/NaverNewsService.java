package com.example.facticle.news.service;

import com.example.facticle.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NaverNewsService {
    private final NewsRepository newsRepository;


    @Value("${naver.api.client-id}")
    private String clientId;

    @Value("${naver.api.client-secret}")
    private String clientSecret;

    private static final String NAVER_NEWS_API_URL = "https://openapi.naver.com/v1/search/news.json";
    private static final int FETCH_INTERVAL_MINUTES = 5; // 5분 간격으로 실행

    @Scheduled(fixedRate = 30000)
    public void fetchNews(){
        
    }
}
