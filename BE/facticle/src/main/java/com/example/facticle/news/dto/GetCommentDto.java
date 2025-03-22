package com.example.facticle.news.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.news.entity.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetCommentDto {
    private Long commentId;
    private Long userId;
    private Long newsId;
    private String nickname;
    private String content;
    private int likeCount;
    private int hateCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Long parentCommentId;
    private List<GetCommentDto> replies;

    //from에서 replies를 직접 comment 파라미터의 replies를 stream으로 가져와 설정하게 되면, lazy로딩으로 인해 1+N 문제가 발생함
    //따라서 replies는 comment 파라미터에서 바로 가져오는 것이 아니라 buildCommentDtoTree 메서드를 활용해 수동으로 replies를 설정하도록 구현
    public static GetCommentDto from(Comment comment){
        return GetCommentDto.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getUser().getUserId())
                .newsId(comment.getNews().getNewsId())
                .nickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .hateCount(comment.getHateCount())
                .createdAt(DateTimeUtil.convertUTCToKST(comment.getCreatedAt()))
                .updatedAt(DateTimeUtil.convertUTCToKST(comment.getUpdatedAt()))
                .parentCommentId((comment.getParentComment() != null) ? comment.getParentComment().getCommentId() : null)
                .replies(new ArrayList<>())
                .build();
    }

    public void updateReplies(List<GetCommentDto> replies) {
        this.replies = replies;
    }
}
