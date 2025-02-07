package com.example.facticle.user.controller;

import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.dto.NicknameCheckDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public BaseResponse localSingUp(@RequestBody @Valid LocalSignupDto localSignupDto){
        Long savedUserId = userService.saveUser(localSignupDto);

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

    @PostMapping("/check-nickname")
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse checkNicknameDuplicate(@RequestBody @Valid NicknameCheckDto nicknameCheckDto) {
        if(userService.checkNickname(nicknameCheckDto.getNickname())){
            return BaseResponse.success(Map.of("code", 200, "is_available", true), "nickname is available.");
        }else{
            return BaseResponse.success(Map.of("code", 200, "is_available", false), "nickname already exists.");
        }
    }

}
