package com.example.facticle.user.controller;

import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.dto.NicknameCheckDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.repository.UserRepository;
import com.example.facticle.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc //@Valid와 같은 검증은 스프링 MVC 요청 바인딩 과정에서 발생 => MockMVC를 사용해 HTTP 요청을 보내야 함
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper(); //Json 직렬화

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp(){
        Long userId1 = userService.saveUser(new LocalSignupRequestDto("user1", "userPassword1!", "nick1"));
        Long userId2 = userService.saveUser(new LocalSignupRequestDto("user2", "userPassword2!", "nick2"));
    }

    @Test
    @DisplayName("로컬회원가입 - 성공")
    void localSingUpTest() throws Exception {
        //given
        LocalSignupRequestDto localSignupRequestDto = new LocalSignupRequestDto("test1", "testtest1!", "nick");


        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(localSignupRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Signup successful."));
    }

    @Test
    @DisplayName("회원가입 실패 - 빈 데이터")
    void localSignupValidationFailTestEmpty() throws Exception {
        // Given: 빈 데이터
        LocalSignupRequestDto emptyDto = new LocalSignupRequestDto("", "", "");

        // When & Then
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyDto)))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

    }

    @Test
    @DisplayName("회원가입 실패 - 규칙 불만족 데이터")
    void localSignupValidationFailTestInvalid() throws Exception {
        // Given
        LocalSignupRequestDto lengthDto = new LocalSignupRequestDto("a", "a1!", "a");

        // When & Then
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lengthDto)))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

        // Given
        LocalSignupRequestDto invalidDto = new LocalSignupRequestDto("aaaaa!", "aaaaaaaaa", "aaaa!");

        // When & Then
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

    }

    @Test
    @DisplayName("ID 중복 검증 - 성공, 실패 모두")
    void checkIdDuplicateTest() throws Exception {
        //검증 1. 중복 x
        UsernameCheckDto usernameCheckDto = new UsernameCheckDto("test_user");

        mockMvc.perform(post("/users/check-username")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usernameCheckDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Username is available."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.is_available").value(true));

        //검증 2. 중복이 있는 경우
        userService.saveUser(new LocalSignupRequestDto("test_user", "Qwer1234!", "test_nick"));

        mockMvc.perform(post("/users/check-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usernameCheckDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("username already exists."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.is_available").value(false));

        UsernameCheckDto invalidDto = new UsernameCheckDto("!");

        mockMvc.perform(post("/users/check-username")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.data.code").value(400));
    }

    @Test
    @DisplayName("ID 중복 검증 - 성공, 실패 모두")
    void checkNicknameDuplicateTest() throws Exception {
        //검증 1. 중복 x
        NicknameCheckDto nicknameCheckDto = new NicknameCheckDto("test_user");

        mockMvc.perform(post("/users/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameCheckDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("nickname is available."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.is_available").value(true));

        //검증 2. 중복이 있는 경우
        userService.saveUser(new LocalSignupRequestDto("test_user", "Qwer1234!", "test_user"));

        mockMvc.perform(post("/users/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nicknameCheckDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("nickname already exists."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.is_available").value(false));

        NicknameCheckDto invalidDto = new NicknameCheckDto("!");

        mockMvc.perform(post("/users/check-nickname")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed."))
                .andExpect(jsonPath("$.data.code").value(400));
    }

    @Test
    @DisplayName("로컬 로그인 테스트 - 성공")
    void localLoginSuccessTest() throws Exception {
        LocalLoginRequestDto localLoginRequestDto = new LocalLoginRequestDto("user1", "userPassword1!");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(localLoginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.access_token").exists())
                .andExpect(jsonPath("$.data.grant_type").value("Bearer"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Secure")));
    }

    @Test
    @DisplayName("로컬 로그인 테스트 - 실패")
    void localLoginFailTest() throws Exception {
        LocalLoginRequestDto failedlocalLoginRequestDto = new LocalLoginRequestDto("faileduser", "userPassword1!");

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(failedlocalLoginRequestDto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Authentication failed. Please check your credentials."))
                .andExpect(jsonPath("$.data.code").value(401));
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    void reCreateTokenSuccessTest() throws Exception {
        TokenInfo tokenInfo = userService.localLogin(new LocalLoginRequestDto("user1", "userPassword1!"));

        mockMvc.perform(post("/users/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("refresh_token", tokenInfo.getRefreshToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token refreshed successfully."))
                .andExpect(jsonPath("$.data.code").value(200))
                .andExpect(jsonPath("$.data.access_token").exists())
                .andExpect(jsonPath("$.data.grant_type").value("Bearer"))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("refresh_token=")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("HttpOnly")))
                .andExpect(header().string("Set-Cookie", org.hamcrest.Matchers.containsString("Secure")));
    }

    @Test
    @DisplayName("토큰 재발급 - 실패")
    void reCreateTokenFailTest() throws Exception {
        //쿠키가 없는 경우
        mockMvc.perform(post("/users/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing required cookie."))
                .andExpect(jsonPath("$.data.code").value(400));

        //토큰이 잘못된 경우
        mockMvc.perform(post("/users/token/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("refresh_token", "Invalid token")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid refresh token."))
                .andExpect(jsonPath("$.data.code").value(401))
                .andExpect(jsonPath("$.data.is_expired").value(false));


    }


}