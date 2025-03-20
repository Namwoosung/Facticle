package com.example.facticle.news.service;

import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.news.dto.NewsSearchCondition;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.repository.jpa.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    public News getNews(Long newsId) {
        //조회 수 관련 로직 추가

        //DTO를 추가해서 댓글정보도 함께 전달
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("news not found", Map.of("newsId", "newsId does not exist.")));
    }

    @Transactional(readOnly = true)
    public List<News> getNewsList(NewsSearchCondition condition){
        return newsRepository.searchNewsList(condition);
    }


}
