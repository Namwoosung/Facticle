package com.example.facticle.user.controller;

import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.dto.NicknameCheckDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

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
    public BaseResponse localLogin(@RequestBody @Valid LocalLoginRequestDto localLoginRequestDto){
        TokenInfo tokenInfo =  userService.localLogin(localLoginRequestDto);
        return BaseResponse.success(Map.of("code", 200, "grant_type", tokenInfo.getGrantType(), "access_token", tokenInfo.getAccessToken(), "refresh_token", tokenInfo.getRefreshToken()), "Login successful.");
    }
}
