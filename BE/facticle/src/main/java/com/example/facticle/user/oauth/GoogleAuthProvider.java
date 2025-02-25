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
public class GoogleAuthProvider implements SocialAuthProvider{
    //추후 만약 대용량 요청을 처리하기 위해 비동기 방식을 사용한다면 WebClient 클래스를 활용하도록 리팩토링 가능
    private final RestTemplate restTemplate; //REST Full API를 호출하기 위한 클래스
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${oauth.google.client-id}")
    private String clientId;
    @Value("${oauth.google.client-secret}")
    private String clientSecret;
    @Value("${oauth.google.redirect-uri}")
    private String redirectUri;

    @Override
    public SocialUserInfo getUserInfo(String authorizationCode) {
        log.debug("authorizationCode: {}", authorizationCode);

        // authorizationCode를 사용하여 액세스 토큰 요청
        // url, header, body 설정
        String tokenUrl = "https://oauth2.googleapis.com/token";
        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("code", authorizationCode);
        tokenParams.add("client_id", clientId);
        tokenParams.add("client_secret", clientSecret);
        tokenParams.add("redirect_uri", redirectUri);
        tokenParams.add("grant_type", "authorization_code");

        //request 생성
        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(tokenParams, tokenHeaders);

        //requset를 보내고, 반환값을 파싱하여 SocialUserInfo를 생성, 아래 과정 중 에러 발생 시 OAuthException으로 처리
        try{
            /* response 반환아래의 내용이 포함되어 있음
             * access_token: 구글의 api 요청에 사용할 수 있는 access token(jwt 형식 그대로는 아니고 자체 형식이 존재하는 듯)
             * expires_in: 엑세스 토큰 유효시간
             * scope: 데이터 접근 범위(open_id, email, profile, ...)
             * token_type: 토큰 타입
             * id_token: openID connect 설정을 한 경우 반환, 사용자의 정보가 들어있는 jwt 토큰
             *
             * 위의 반환값에서 access token을 이용해서 사용자 정보 조회 google api를 다시 요청해도 되지만, 현재 상황에서는 id_token에서 사용자 정보를 추출해서 바로 사용해도 무방
             * 또한 id_token의 유효성을 따로 검증할 필요도 없음
             * 구글 공식문서(https://developers.google.com/identity/openid-connect/openid-connect?hl=ko#authenticatingtheuser)의 서버흐름 파트를 봐도 https를 통해 직접 id_token을 받은 경우에는 굳이 검증을 하지 않아도 된다고 밝힘
             * 만약 이 id_token을 다른 앱에서 받았거나, 다른 구성요소로 보낼 경우에는 검증이 필요하지만 현재 상황에서 id_token은 유효하다고 확신할 수 있기에 굳이 검증이 필요없음
             */
            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(
                    tokenUrl, tokenRequest, Map.class);
            Map<String, Object> tokenBody = tokenResponse.getBody();
            String idToken = (String) tokenBody.get("id_token");
            if(idToken == null){
                throw new OAuthException("Google response did not include id_token");
            }

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
                    .provider("google")
                    .build();

        }catch (HttpClientErrorException e){
            throw new OAuthException("Failed to google authorization code validation because of client input");
        }catch (HttpServerErrorException e){
            throw new OAuthException("Failed to google authorization code validation because of server error");
        }catch (ParseException e) {
            throw new OAuthException("Failed to parse id_token");
        } catch (RuntimeException e){
            throw new OAuthException("Failed to google authorization code validation");
        }
    }

    @Override
    public boolean support(String provider) {
        return "google".equalsIgnoreCase(provider);
    }
}
