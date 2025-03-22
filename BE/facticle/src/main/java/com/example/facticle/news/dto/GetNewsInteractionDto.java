package com.example.facticle.news.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.entity.NewsInteraction;
import com.example.facticle.news.entity.ReactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetNewsInteractionDto {
    private Long newsInteractionId;
    private Long userId;
    private Long newsId;

    private ReactionType reaction;
    private BigDecimal rating;

    private LocalDateTime reactionAt;
    private LocalDateTime ratedAt;
    private LocalDateTime viewedAt;

    public static GetNewsInteractionDto from(NewsInteraction newsInteraction){
        return GetNewsInteractionDto.builder()
                .newsInteractionId(newsInteraction.getNewsInteractionId())
                .userId(newsInteraction.getUser().getUserId())
                .newsId(newsInteraction.getNews().getNewsId())
                .reaction(newsInteraction.getReaction())
                .rating(newsInteraction.getRating())
                .reactionAt(DateTimeUtil.convertUTCToKST(newsInteraction.getReactionAt()))
                .ratedAt(DateTimeUtil.convertUTCToKST(newsInteraction.getRatedAt()))
                .viewedAt(DateTimeUtil.convertUTCToKST(newsInteraction.getViewedAt()))
                .build();
    }
}
