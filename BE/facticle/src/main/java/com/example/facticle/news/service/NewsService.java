package com.example.facticle.news.service;

import com.example.facticle.news.entity.News;
import com.example.facticle.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    @Transactional(readOnly = true)
    public News getNews(Long newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalArgumentException("news not found"));
    }
}
