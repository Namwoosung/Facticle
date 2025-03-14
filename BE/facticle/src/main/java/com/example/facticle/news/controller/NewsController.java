package com.example.facticle.news.controller;

import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.news.dto.GetNewsResponseDto;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/{newsId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getNews(@PathVariable Long newsId){
        News news = newsService.getNews(newsId);
        GetNewsResponseDto newsResponseDto = GetNewsResponseDto.from(news);

        return BaseResponse.success(Map.of("code", 200, "news", newsResponseDto), "news retrieved successfully.");
    }

}
