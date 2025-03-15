package com.example.facticle.news.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.entity.News;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class NewsListResponseDto {
    private Long newsId;
    private String imageUrl;
    private String title;
    private String summary;
    private BigDecimal headlineScore;
    private BigDecimal factScore;
    private LocalDateTime collectedAt;
    private int likeCount;
    private int hateCount;
    private int commentCount;
    private int viewCount;
    private int ratingCount;
    private BigDecimal rating;

    public static NewsListResponseDto from(News  news){
        return NewsListResponseDto.builder()
                .newsId(news.getNewsId())
                .imageUrl(news.getImageUrl())
                .title(news.getTitle())
                .summary(news.getSummary())
                .headlineScore(news.getHeadlineScore())
                .factScore(news.getFactScore())
                .collectedAt(DateTimeUtil.convertUTCToKST(news.getCollectedAt()))
                .likeCount(news.getLikeCount())
                .hateCount(news.getHateCount())
                .commentCount(news.getCommentCount())
                .viewCount(news.getViewCount())
                .ratingCount(news.getRatingCount())
                .rating(news.getRating())
                .build();
    }
}
