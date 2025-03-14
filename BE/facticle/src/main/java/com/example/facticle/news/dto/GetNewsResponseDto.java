package com.example.facticle.news.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetNewsResponseDto {
    private Long newsId;
    private String url;
    private String naverUrl;
    private String title;
    private String summary;
    private String imageUrl;
    private String mediaName;
    private NewsCategory category;

    private BigDecimal headlineScore;
    private BigDecimal factScore;
    private String headlineScoreReason;
    private String factScoreReason;
    private LocalDateTime collectedAt;

    private int likeCount;
    private int hateCount;
    private int commentCount;
    private int viewCount;
    private int ratingCount;
    private BigDecimal rating;

    public static GetNewsResponseDto from(News news){
        return GetNewsResponseDto.builder()
                .newsId(news.getNewsId())
                .url(news.getUrl())
                .naverUrl(news.getNaverUrl())
                .title(news.getTitle())
                .summary(news.getSummary())
                .imageUrl(news.getImageUrl())
                .mediaName(news.getMediaName())
                .category(news.getCategory())
                .headlineScore(news.getHeadlineScore())
                .factScore(news.getFactScore())
                .headlineScoreReason(news.getHeadlineScoreReason())
                .factScoreReason(news.getFactScoreReason())
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
