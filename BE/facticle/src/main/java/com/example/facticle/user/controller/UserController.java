package com.example.facticle.user.controller;

import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.entity.LocalAuth;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 자체 회원 가입 api
     */
    @PostMapping("/signup")
    public BaseResponse localSingUp(@RequestBody @Valid LocalSignupDto localSignupDto){
        Long savedUserId = userService.saveUser(localSignupDto);

        Map<String, Object> data = new HashMap<>();
        data.put("code", 201);
        data.put("user_id", savedUserId);

        return BaseResponse.success(data, "Signup successful.");
    }


}
