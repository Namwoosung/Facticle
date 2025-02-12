package com.example.facticle.user.controller;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.dto.NicknameCheckDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 자체 회원 가입 api
     */
    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public BaseResponse localSingUp(@RequestBody @Valid LocalSignupRequestDto localSignupRequestDto){
        Long savedUserId = userService.saveUser(localSignupRequestDto);

        Map<String, Object> data = new HashMap<>();
        data.put("code", 201);
        data.put("user_id", savedUserId);

        return BaseResponse.success(data, "Signup successful.");
    }

    /**
     * ID 중복체크
     */
    @PostMapping("/check-username")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse checkIdDuplicate(@RequestBody @Valid UsernameCheckDto usernameCheckDto) {
        if(userService.checkUsername(usernameCheckDto.getUsername())){
            return BaseResponse.success(Map.of("code", 200, "is_available", true), "Username is available.");
        }else{
            return BaseResponse.success(Map.of("code", 200, "is_available", false), "username already exists.");
        }
    }

    /**
     * 넥네임 중복체크
     */
    @PostMapping("/check-nickname")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse checkNicknameDuplicate(@RequestBody @Valid NicknameCheckDto nicknameCheckDto) {
        if(userService.checkNickname(nicknameCheckDto.getNickname())){
            return BaseResponse.success(Map.of("code", 200, "is_available", true), "nickname is available.");
        }else{
            return BaseResponse.success(Map.of("code", 200, "is_available", false), "nickname already exists.");
        }
    }


    /**
     * 로컬 로그인
     */
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse localLogin(@RequestBody @Valid LocalLoginRequestDto localLoginRequestDto, HttpServletResponse response){
        //로그인 수행
        TokenInfo tokenInfo =  userService.localLogin(localLoginRequestDto);

        // refresh token은 http only secure 쿠키에 저장
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenInfo.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/users") //유저관련 api에 한정하여 전송
                .maxAge(Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidTime()).getSeconds()) //밀리초와 초 단위를 맞춰줌
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return BaseResponse.success(Map.of("code", 200, "grant_type", tokenInfo.getGrantType(), "access_token", tokenInfo.getAccessToken()), "Login successful.");
    }

    /**
     * token 재발급 api
     */
    @PostMapping("/token/refresh")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse reCreateToken(@CookieValue(name = "refresh_token", required = true) String refreshToken, HttpServletResponse response){
        TokenInfo tokenInfo = userService.reCreateToken(refreshToken);


        // refresh token은 http only secure 쿠키에 저장
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", tokenInfo.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/users") //유저관련 api에 한정하여 전송
                .maxAge(Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidTime()).getSeconds()) //밀리초와 초 단위를 맞춰줌
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return BaseResponse.success(Map.of("code", 200, "grant_type", tokenInfo.getGrantType(), "access_token", tokenInfo.getAccessToken()), "Token refreshed successfully.");
    }


    /**
     * 로그 아웃
     */
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse logout(@CookieValue(name = "refresh_token", required = true) String refreshToken, HttpServletResponse response){
        userService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/users")
                .maxAge(0) // 쿠키 삭제
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", deleteCookie.toString());

        return BaseResponse.success(Map.of("code", 200), "Logout successful.");
    }


    /**
     * 마이 페이지 조회(지금은 wjt 인증 테스트를 위해 임시 생성)
     */
    @GetMapping("/mypage")
    @ResponseStatus(HttpStatus.OK)
    public String getMyPage(){
        return "ok";
    }

    /**
     * 어드민 관련 기능(지금은 wjt 인증 테스트를 위해 임시 생성)
     */
    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public String getAdmin(){
        return "ok";
    }

}
