package com.example.facticle.news.dto;

import com.example.facticle.news.entity.NewsCategory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class NewsSearchCondition {
    private String titleKeyword; // 제목 키워드
    private String contentKeyword; // 본문 키워드
    private String titleOrContentKeyword; // 제목 또는 본문 키워드


    private List<String> publishers; // 언론사 목록
    private List<NewsCategory> categories; // 카테고리 목록

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate; // 시작 날짜
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate; // 종료 날짜

    private BigDecimal minHsScore;
    private BigDecimal maxHsScore;
    private BigDecimal minFsScore;
    private BigDecimal maxFsScore;

    //좋아요, 싫어요, 댓글, 조회 수, 별점 등의 검색 조건은 추후 뉴스 상호작용 기능 개발 후 다시 한번 더 테스트
    private Integer minLikes;
    private Integer maxLikes;
    private Integer minDislikes;
    private Integer maxDislikes;

    private Integer minComments;
    private Integer maxComments;
    private Integer minViews;
    private Integer maxViews;
    private Integer minRatingCount;
    private Integer maxRatingCount;
    private BigDecimal minRating;
    private BigDecimal maxRating;

    @Builder.Default
    private SortBy sortBy = SortBy.COLLECTED_AT; // 정렬 기준
    @Builder.Default
    private SortDirection sortDirection = SortDirection.DESC; // ASC 또는 DESC

    @Min(value = 0, message = "Page value cannot be negative")
    @Builder.Default
    private Integer page = 0; // 기본값 0
    @Min(value = 1, message = "Paging size must be at least 1")
    @Max(value = 100, message = "Paging size must be at most 100")
    @Builder.Default
    private Integer size = 10; // 기본값 10

    @AssertTrue(message = "Start date cannot be after end date")
    private boolean isDateRangeValid() {
        return startDate == null || endDate == null || !startDate.isAfter(endDate);
    }
}
