package com.example.facticle.news.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.entity.CommentInteraction;
import com.example.facticle.news.entity.ReactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetCommentInteractionDto {
    private Long commentInteractionId;
    private Long userId;
    private Long commentId;
    private ReactionType reaction;
    private LocalDateTime reactionAt;

    public static GetCommentInteractionDto from(CommentInteraction commentInteraction){
        return GetCommentInteractionDto.builder()
                .commentInteractionId(commentInteraction.getCommentInteractionId())
                .userId(commentInteraction.getUser().getUserId())
                .commentId(commentInteraction.getComment().getCommentId())
                .reaction(commentInteraction.getReaction())
                .reactionAt(DateTimeUtil.convertUTCToKST(commentInteraction.getReactionAt()))
                .build();
    }

}
