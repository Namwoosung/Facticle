package com.example.facticle.user.oauth;

import com.example.facticle.user.entity.SocialUserInfo;

public interface SocialAuthProvider {

    SocialUserInfo getUserInfo(String authorizationCode);
    boolean support(String provider);
}
