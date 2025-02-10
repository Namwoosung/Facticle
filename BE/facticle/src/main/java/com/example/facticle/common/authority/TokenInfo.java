package com.example.facticle.common.authority;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TokenInfo {
    private String grantType; //jwt 권한 인증 타입
    private String accessToken;
    private String refreshToken;
}
