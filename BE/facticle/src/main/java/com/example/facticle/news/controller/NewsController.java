package com.example.facticle.news.controller;

import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.dto.*;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    /**
     * 개별 뉴스 상세 조회
     */
    @GetMapping("/{newsId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getNews(@PathVariable Long newsId){
        News news = newsService.getNews(newsId);
        GetNewsResponseDto newsResponseDto = GetNewsResponseDto.from(news);

        return BaseResponse.success(Map.of("code", 200, "news", newsResponseDto), "news retrieved successfully.");
    }

    /**
     * 뉴스 리스트 조회(검색 조건 기반)
     */
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse getNewsList(@ModelAttribute @Valid NewsSearchCondition condition){
        //default value 명시적 설정
        if (condition.getSortBy() == null) {
            condition.setSortBy(SortBy.COLLECTED_AT);
        }
        if (condition.getSortDirection() == null) {
            condition.setSortDirection(SortDirection.DESC);
        }
        if (condition.getPage() == null) {
            condition.setPage(0);
        }
        if (condition.getSize() == null) {
            condition.setSize(10);
        }

        //KST 시간을 UTC로 변환
        if(condition.getStartDate() != null){
            condition.setStartDate(DateTimeUtil.convertKSTToUTC(condition.getStartDate()));
        }
        if(condition.getEndDate() != null){
            condition.setEndDate(DateTimeUtil.convertKSTToUTC(condition.getEndDate()));
        }

        List<News> newsList =  newsService.getNewsList(condition);

        List<NewsListResponseDto> newsListResponseDtos =
                newsList.stream()
                        .map(NewsListResponseDto::from)
                        .toList();

        return BaseResponse.success(Map.of("code", 200, "totalCount", newsListResponseDtos.size(), "newsList", newsListResponseDtos), "Search results retrieved successfully.");

    }

}
