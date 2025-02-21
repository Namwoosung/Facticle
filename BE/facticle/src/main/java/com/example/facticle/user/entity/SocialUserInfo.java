package com.example.facticle.user.entity;


import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class SocialUserInfo {
    String socialId;
    String provider;
    String email;
}
