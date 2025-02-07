package com.example.facticle.user.service;

import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.entity.SignupType;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.entity.UserRole;
import com.example.facticle.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("local")
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


    @Test
    void saveUserTest() {
        //given
        LocalSignupDto localSignupDto = new LocalSignupDto("testUser", "qwerqwer1!", "테스트");

        //when
        Long userId = userService.saveUser(localSignupDto);

        //then
        User findUser = userRepository.findById(userId).get();
        Assertions.assertThat(findUser.getLocalAuth().getUsername()).isEqualTo(localSignupDto.getUsername());
        Assertions.assertThat(passwordEncoder.matches(localSignupDto.getPassword(), findUser.getLocalAuth().getHashedPassword())).isTrue();
        Assertions.assertThat(findUser.getNickname()).isEqualTo(localSignupDto.getNickname());
        Assertions.assertThat(findUser.getRole()).isEqualTo(UserRole.USER);
        Assertions.assertThat(findUser.getSignupType()).isEqualTo(SignupType.LOCAL);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 데이터")
    void saveUserFailTest(){

        LocalSignupDto localSignupDto = new LocalSignupDto("testUser", "qwerqwer1!", "테스트");

        userService.saveUser(localSignupDto);

        Assertions.assertThatThrownBy(() -> userService.saveUser(localSignupDto))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("Invalid input")
                .satisfies(ex -> {
                    InvalidInputException e = (InvalidInputException) ex;
                    Assertions.assertThat(e.getErrors()).containsKey("username");
                    Assertions.assertThat(e.getErrors()).containsKey("nickname");
                });
    }

    @Test
    @DisplayName("아이디 중복 체크 - 성공, 실패 모두")
    void checkUsernameTest(){
        Assertions.assertThat(userService.checkUsername("test_user")).isTrue();

        userService.saveUser(new LocalSignupDto("test_user", "Qwer1234!", "test_nick"));
        Assertions.assertThat(userService.checkUsername("test_user")).isFalse();
    }
}