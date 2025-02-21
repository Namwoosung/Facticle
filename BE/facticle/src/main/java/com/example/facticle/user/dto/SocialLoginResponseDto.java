package com.example.facticle.user.dto;

import com.example.facticle.common.authority.TokenInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SocialLoginResponseDto {
    TokenInfo tokenInfo;
    boolean isNew;
}
