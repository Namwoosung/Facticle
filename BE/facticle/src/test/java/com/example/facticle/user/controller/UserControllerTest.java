package com.example.facticle.user.controller;

import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.dto.NicknameCheckDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Transactional
@AutoConfigureMockMvc //@Valid와 같은 검증은 스프링 MVC 요청 바인딩 과정에서 발생 => MockMVC를 사용해 HTTP 요청을 보내야 함
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper(); //Json 직렬화

    @Autowired
    UserService userService;

    @Test
    @DisplayName("로컬회원가입 - 성공")
    void localSingUpTest() throws Exception {
        //given
        LocalSignupDto localSignupDto = new LocalSignupDto("test1", "testtest1!", "nick");


        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(localSignupDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Signup successful."));
    }

    @Test
    @DisplayName("회원가입 실패 - 빈 데이터")
    void localSignupValidationFailTestEmpty() throws Exception {
        // Given: 빈 데이터
        LocalSignupDto emptyDto = new LocalSignupDto("", "", "");

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
        LocalSignupDto lengthDto = new LocalSignupDto("a", "a1!", "a");

        // When & Then
        mockMvc.perform(post("/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(lengthDto)))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

        // Given
        LocalSignupDto invalidDto = new LocalSignupDto("aaaaa!", "aaaaaaaaa", "aaaa!");

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
        userService.saveUser(new LocalSignupDto("test_user", "Qwer1234!", "test_nick"));

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
        userService.saveUser(new LocalSignupDto("test_user", "Qwer1234!", "test_user"));

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


}