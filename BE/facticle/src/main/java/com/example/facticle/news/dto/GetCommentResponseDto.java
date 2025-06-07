package com.example.facticle.news.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetCommentResponseDto {
    private boolean isUser;
    private List<GetCommentDto> getCommentDtos;
    private List<GetCommentInteractionDto> getCommentInteractionDtos;
}
