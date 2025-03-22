package com.example.facticle.news.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SetCommentDto {
    @NotBlank(message = "content must not be empty")
    private String content;
}
