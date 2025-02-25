package com.example.facticle.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialLoginRequestDto {

    @NotBlank(message = "social provider is required.")
    @Pattern(regexp = "google|kakao|naver", message = "UnSupported provider.")
    String provider;

    @NotBlank(message = "authorization code id required.")
    String code;
}
