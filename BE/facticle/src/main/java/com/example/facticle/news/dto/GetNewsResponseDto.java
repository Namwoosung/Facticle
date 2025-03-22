package com.example.facticle.news.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetNewsResponseDto {
    private boolean isUser;
    private GetNewsDto getNewsDto;
    private GetNewsInteractionDto getNewsInteractionDto;
    private List<GetCommentDto> getCommentDtos;
    private List<GetCommentInteractionDto> getCommentInteractionDtos;
}
