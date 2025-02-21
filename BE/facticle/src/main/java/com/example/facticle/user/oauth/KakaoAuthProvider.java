package com.example.facticle.user.oauth;

import com.example.facticle.common.exception.OAuthException;
import com.example.facticle.user.entity.SocialUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAuthProvider implements SocialAuthProvider{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;


    @Override
    public SocialUserInfo getUserInfo(String authorizationCode) {
        log.debug("authorizationCode: {}", authorizationCode);

        //authorization code로 access token을 발급받는 과정
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("code", authorizationCode);
        tokenParams.add("client_id", clientId);
        tokenParams.add("client_secret", clientSecret);
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(tokenParams, tokenHeaders);

        try{
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                    tokenUrl, tokenRequest, Map.class);
            Map<String, Object> tokenBody = tokenResponse.getBody();
            String idToken = (String) tokenBody.get("id_token");
            if(idToken == null){
                throw new OAuthException("kakao response did not include id_token");
            }

            log.debug("Obtained id Token: {}", idToken);

            //획득한 id_token으로 유저정보 파싱
            //jwt 파싱을 위해 SignedJWT 클래스 사용
            SignedJWT signedJWT = SignedJWT.parse(idToken);

            Map<String, Object> userInfo = signedJWT.getJWTClaimsSet().getClaims();

            // 파싱한 데이터에서 필요한 유저 정보 획득
            String email = userInfo.containsKey("email") ? (String) userInfo.get("email") : null; //이메일은 필드가 존재하는 경우에만 입력
            String socialId = (String) userInfo.get("sub");

            // 사용자 정보를 SocialUserInfo 객체로 변환하여 반환
            return SocialUserInfo.builder()
                    .socialId(socialId)
                    .email(email)
                    .provider("kakao")
                    .build();

        }catch (HttpClientErrorException e){
            throw new OAuthException("Failed to kakao authorization code validation because of client input");
        }catch (HttpServerErrorException e){
            throw new OAuthException("Failed to kakao authorization code validation because of server error");
        }catch (ParseException e) {
            throw new OAuthException("Failed to parse id_token");
        } catch (RuntimeException e){
            throw new OAuthException("Failed to kakao authorization code validation");
        }

    }

    @Override
    public boolean support(String provider) {
        return "kakao".equalsIgnoreCase(provider);
    }
}
