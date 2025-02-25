package com.example.facticle.user.oauth;

import com.example.facticle.common.exception.OAuthException;
import com.example.facticle.user.entity.SocialUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverAuthProvider implements SocialAuthProvider{
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${oauth.naver.client-id}")
    private String clientId;
    @Value("${oauth.naver.client-secret}")
    private String clientSecret;
    @Value("${oauth.naver.redirect-uri}")
    private String redirectUri;

    @Override
    public SocialUserInfo getUserInfo(String authorizationCode) {
        log.debug("authorizationCode: {}", authorizationCode);

        //authorization code로 access token을 발급받는 과정
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //네이버는 redirect_uri대신 state가 필요(공식문서 참고), 즉 구글은 authorization code 발급과 검증 과정에서 동일한 서비스라는 것을 검증하기 위해 redirect_uri가 동일한 지 검증했다면, 네이버는 이 state 값으로 동일한지 검증
        // (이 state 값을 프론트와 다른 값을 써도 문제 없이 access token이 발급되는 거롤 보아 네이버는 authorization code 검증 과정에서는 딱히 state 값을 검증하지 않는 것 같지만, 그래도 OAuth 과정을 준수하기 위해 가능하면 프론트와 동일한 state 값을 포함하는 것이 적절해 보임)
        String state = UUID.randomUUID().toString();

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("code", authorizationCode);
        tokenParams.add("client_id", clientId);
        tokenParams.add("client_secret", clientSecret);
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("state", state);

        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(tokenParams, tokenHeaders);

        try{
            //response에서 access token 추출
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                    tokenUrl, tokenRequest, Map.class);
            Map<String, Object> tokenBody = tokenResponse.getBody();
            if (tokenBody == null || !tokenBody.containsKey("access_token")) {
                throw new OAuthException("Failed to get access token from Naver");
            }

            String accessToken = (String) tokenBody.get("access_token");
            log.debug("Obtained Access Token: {}", accessToken);

            //네이버의 경우 authorization code 검증 후 id_token을 제공하는 것이 아니라 access token을 제공
            //획득한 access token으로 회원 프로필 조회 api를 조회해서 회원 정보 획득 가능
            //access token을 헤더로 넣어 유저 정보 조회 api 호출
            String userInfoUrl = "https://openapi.naver.com/v1/nid/me";

            HttpHeaders infoHeaders = new HttpHeaders();
            infoHeaders.setBearerAuth(accessToken);

            HttpEntity<Void> infoRequest = new HttpEntity<>(infoHeaders);

            ResponseEntity<Map> infoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, infoRequest, Map.class);
            Map<String, Object> infoBody = infoResponse.getBody();
            if(infoBody == null || !infoBody.containsKey("response")){
                throw new OAuthException("Failed to get user info from Naver");
            }

            //응답 데이터에서 사용자 정보 추출
            Map<String, Object> userData = (Map<String, Object>) infoBody.get("response");

            log.debug("userData: {}", userData);

            String socialId = (String) userData.get("id");
            String email = userData.containsKey("email") ? (String) userData.get("email") : null;

            return SocialUserInfo.builder()
                    .socialId(socialId)
                    .email(email)
                    .provider("naver")
                    .build();

        }catch (HttpClientErrorException e){
            throw new OAuthException("Failed to naver authorization code validation because of client input");
        }catch (HttpServerErrorException e){
            throw new OAuthException("Failed to naver authorization code validation because of server error");
        }catch (RuntimeException e){
            throw new OAuthException("Failed to naver authorization code validation");
        }

    }

    @Override
    public boolean support(String provider) {
        return "naver".equalsIgnoreCase(provider);
    }
}
