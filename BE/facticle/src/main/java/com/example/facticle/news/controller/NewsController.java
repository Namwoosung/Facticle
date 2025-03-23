package com.example.facticle.news.controller;

import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.common.dto.CustomUserDetails;
import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.dto.*;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.service.NewsService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public BaseResponse getNews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId,
            @CookieValue(name = "viewed_news_ids", required = false) String viewedNewsIds,
            HttpServletResponse response
    ){

        Long userId = (customUserDetails != null) ? customUserDetails.getUserId() : null;

        GetNewsResponseDto responseDto = newsService.getNewsPage(newsId, userId, viewedNewsIds, response);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("isUser", responseDto.isUser());
        result.put("news", responseDto.getGetNewsDto());
        result.put("comment", responseDto.getGetCommentDtos());

        if(userId != null){
            result.put("newsInteraction", responseDto.getGetNewsInteractionDto());
            result.put("commentInteraction", responseDto.getGetCommentInteractionDtos());
        }

        return BaseResponse.success(result, "news retrieved successfully.");
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

    @PostMapping("/{newsId}/like")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse likeNews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId
    ){

        newsService.likeNews(newsId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "Like added successfully.");
    }

    @DeleteMapping("/{newsId}/like")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse unlikeNews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId
    ){
        newsService.unlikeNews(newsId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "Like canceled successfully.");
    }

    @PostMapping("/{newsId}/hate")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse hateNews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId
    ){

        newsService.hateNews(newsId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "hate added successfully.");
    }

    @DeleteMapping("/{newsId}/hate")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse unhateNews(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId
    ){
        newsService.unhateNews(newsId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "hate canceled successfully.");
    }

    @PostMapping("{newsId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse addComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId,
            @RequestBody @Valid SetCommentDto setCommentDto
    ){
        GetCommentDto getCommentDto =  newsService.createComment(newsId, customUserDetails.getUserId(), setCommentDto.getContent());

        return BaseResponse.success(Map.of("code", 201, "comment", getCommentDto), "add comment successfully.");
    }

    @PatchMapping("{newsId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId,
            @PathVariable Long commentId,
            @RequestBody @Valid SetCommentDto setCommentDto
    ){
        GetCommentDto getCommentDto =  newsService.updateComment(newsId, commentId, customUserDetails.getUserId(), setCommentDto.getContent());

        return BaseResponse.success(Map.of("code", 200, "comment", getCommentDto), "update comment successfully.");
    }

    @DeleteMapping("{newsId}/comment/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId,
            @PathVariable Long commentId
    ){
        newsService.deleteComment(newsId, commentId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "delete comment successfully.");
    }

    @PostMapping("{newsId}/comment/{parentCommentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse addReplyComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long newsId,
            @PathVariable Long parentCommentId,
            @RequestBody @Valid SetCommentDto setCommentDto
    ){
        GetCommentDto getCommentDto =  newsService.createReplyComment(newsId, parentCommentId,customUserDetails.getUserId(), setCommentDto.getContent());

        return BaseResponse.success(Map.of("code", 201, "comment", getCommentDto), "add comment successfully.");
    }

    @PostMapping("/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse likeComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long commentId
    ){
        newsService.likeComment(commentId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "comment like interaction added successfully.");
    }

    @DeleteMapping("/comment/{commentId}/like")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse unlikeComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long commentId
    ){
        newsService.unlikeComment(commentId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "comment like interaction canceled successfully.");
    }

    @PostMapping("/comment/{commentId}/hate")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse hateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long commentId
    ){
        newsService.hateComment(commentId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "comment hate interaction added successfully.");
    }

    @DeleteMapping("/comment/{commentId}/hate")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse unhateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long commentId
    ){
        newsService.unhateComment(commentId, customUserDetails.getUserId());

        return BaseResponse.success(Map.of("code", 200), "comment hate interaction canceled successfully.");
    }
}
