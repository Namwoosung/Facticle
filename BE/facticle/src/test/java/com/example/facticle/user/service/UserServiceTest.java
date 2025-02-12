package com.example.facticle.user.service;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.common.authority.TokenValidationResult;
import com.example.facticle.common.dto.CustomUserDetails;
import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.common.exception.InvalidTokenException;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.entity.*;
import com.example.facticle.user.repository.RefreshTokenRepository;
import com.example.facticle.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    EntityManager entityManager;

    private User user1;
    private User user2;
    String refreshToken1Expire;
    String refreshToken1Revoke;
    String refreshToken1Valid;


    @BeforeEach
    void setUp() throws InterruptedException {
        user1 = User.builder()
                .nickname("nick1")
                .localAuth(new LocalAuth("user1", passwordEncoder.encode("userPassword1!")))
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();

        user2 = User.builder()
                .nickname("nick2")
                .localAuth(new LocalAuth("user2", passwordEncoder.encode("userPassword2!")))
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();

        userRepository.save(user1);
        userRepository.save(user2);

        CustomUserDetails customUserDetails = new CustomUserDetails(
                user1.getUserId(),
                user1.getLocalAuth().getUsername(),
                "",
                List.of(new SimpleGrantedAuthority("ROLE_" + user1.getRole().name()))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, "", customUserDetails.getAuthorities());

        refreshToken1Expire = jwtTokenProvider.createRefreshToken(authentication);
        RefreshToken storedRefreshToken1Expire = RefreshToken.builder()
                .user(user1)
                .hashedRefreshToken(passwordEncoder.encode(refreshToken1Expire))
                .isRevoked(false)
                .issuedAt(LocalDateTime.now().minusMinutes(40))
                .expiresAt(LocalDateTime.now().minusMinutes(10)) //이미 만료
                .build();
        refreshTokenRepository.save(storedRefreshToken1Expire);

        refreshToken1Revoke = jwtTokenProvider.createRefreshToken(authentication);
        RefreshToken storedRefreshToken1Revoke = RefreshToken.builder()
                .user(user1)
                .hashedRefreshToken(passwordEncoder.encode(refreshToken1Revoke))
                .isRevoked(true)
                .issuedAt(LocalDateTime.now().minusMinutes(10))
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        refreshTokenRepository.save(storedRefreshToken1Revoke);

        refreshToken1Valid = jwtTokenProvider.createRefreshToken(authentication);
        RefreshToken storedRefreshToken1Valid = RefreshToken.builder()
                .user(user1)
                .hashedRefreshToken(passwordEncoder.encode(refreshToken1Valid))
                .isRevoked(false)
                .issuedAt(LocalDateTime.now().minusMinutes(5))
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        refreshTokenRepository.save(storedRefreshToken1Valid);
    }


    @Test
    void saveUserTest() {
        //given
        LocalSignupRequestDto localSignupRequestDto = new LocalSignupRequestDto("testUser", "qwerqwer1!", "테스트");

        //when
        Long userId = userService.saveUser(localSignupRequestDto);

        //then
        User findUser = userRepository.findById(userId).get();
        Assertions.assertThat(findUser.getLocalAuth().getUsername()).isEqualTo(localSignupRequestDto.getUsername());
        Assertions.assertThat(passwordEncoder.matches(localSignupRequestDto.getPassword(), findUser.getLocalAuth().getHashedPassword())).isTrue();
        Assertions.assertThat(findUser.getNickname()).isEqualTo(localSignupRequestDto.getNickname());
        Assertions.assertThat(findUser.getRole()).isEqualTo(UserRole.USER);
        Assertions.assertThat(findUser.getSignupType()).isEqualTo(SignupType.LOCAL);
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 데이터")
    void saveUserFailTest(){

        LocalSignupRequestDto localSignupRequestDto = new LocalSignupRequestDto("user1", "qwerqwer1!", "nick1");


        Assertions.assertThatThrownBy(() -> userService.saveUser(localSignupRequestDto))
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
        Assertions.assertThat(userService.checkUsername("user1")).isFalse();
    }

    @Test
    @DisplayName("닉네임 중복 체크 - 성공, 실패 모두")
    void checkNicknameTest(){
        Assertions.assertThat(userService.checkNickname("test_user")).isTrue();
        Assertions.assertThat(userService.checkNickname("nick1")).isFalse();
    }

    @Test
    @DisplayName("로컬 로그인 테스트 - 아이디 혹은 비밀번호 오류")
    void localLoginFailTest(){
        LocalLoginRequestDto localLoginRequestDto1 = new LocalLoginRequestDto("failedUser", "userPassword1!");

        Assertions.assertThatThrownBy(() -> userService.localLogin(localLoginRequestDto1))
                .isInstanceOf(BadCredentialsException.class);

        LocalLoginRequestDto localLoginRequestDto2 = new LocalLoginRequestDto("user2", "failedPassword1!");

        Assertions.assertThatThrownBy(() -> userService.localLogin(localLoginRequestDto2))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("로컬 로그인 테스트 - 성공")
    void localLoginSuccessTest(){
        TokenInfo tokenInfo1 = userService.localLogin(new LocalLoginRequestDto("user1", "userPassword1!"));

        Assertions.assertThat(tokenInfo1.getGrantType()).isEqualTo("Bearer");
        Assertions.assertThat(jwtTokenProvider.validateToken(tokenInfo1.getAccessToken())).isEqualTo(TokenValidationResult.VALID);
        Assertions.assertThat(jwtTokenProvider.validateToken(tokenInfo1.getRefreshToken())).isEqualTo(TokenValidationResult.VALID);
    }

    @Test
    @DisplayName("토큰 재발급 - 성공")
    void reCreateTokenSuccessTest(){
        TokenInfo tokenInfo1 = userService.reCreateToken(refreshToken1Valid);

        Assertions.assertThat(tokenInfo1.getGrantType()).isEqualTo("Bearer");
        Assertions.assertThat(jwtTokenProvider.validateToken(tokenInfo1.getAccessToken())).isEqualTo(TokenValidationResult.VALID);
        Assertions.assertThat(jwtTokenProvider.validateToken(tokenInfo1.getRefreshToken())).isEqualTo(TokenValidationResult.VALID);
    }

    @Test
    @DisplayName("토큰 재발급 - 실패")
    void reCreateTokenFailTest(){
        Assertions.assertThatThrownBy(() -> userService.reCreateToken("Invalid Token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Invalid refresh token.");
        Assertions.assertThatThrownBy(() -> userService.reCreateToken(refreshToken1Expire))
                .isInstanceOf(InvalidTokenException.class);
        Assertions.assertThatThrownBy(() -> userService.reCreateToken(refreshToken1Revoke))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("로그아웃 - 성공")
    void logoutSuccessTest(){
        RefreshToken refreshToken = refreshTokenRepository.findValidTokenByUser(user1).get();
        Assertions.assertThat(passwordEncoder.matches(refreshToken1Valid, refreshToken.getHashedRefreshToken())).isTrue();

        userService.logout(refreshToken1Valid);

        entityManager.flush();
        entityManager.clear();

        Optional<RefreshToken> validTokenByUser = refreshTokenRepository.findValidTokenByUser(user1);
        Assertions.assertThat(validTokenByUser).isEmpty();
        List<RefreshToken> revokedTokens = refreshTokenRepository.findByUser(user1);
        Assertions.assertThat(revokedTokens).allMatch(RefreshToken::isRevoked);

    }

    @Test
    @DisplayName("로그아웃 - 실패")
    void logoutFailTest(){
        //잘못된 토큰
        Assertions.assertThatThrownBy(() -> userService.logout("Invalid Token"))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Failed to extract user from token");

        //만료된 토큰 혹은 revoke된 토큰으로 조회 시에도 동일하게 해당 user의 모든 refresh token을 revoke(공격 방지)
        RefreshToken refreshToken = refreshTokenRepository.findValidTokenByUser(user1).get();
        Assertions.assertThat(passwordEncoder.matches(refreshToken1Valid, refreshToken.getHashedRefreshToken())).isTrue(); //처음엔 조회 성공

        userService.logout(refreshToken1Expire);
        entityManager.flush();
        entityManager.clear(); //테스트 환경에서 트랜잭션 전파 발생 -> DB 변경 후에 DB와 영속성 컨텍스트가 불일치 할 수 있기에, 영속성 컨텍스트를 수동으로 clear

        Optional<RefreshToken> validTokenByUser = refreshTokenRepository.findValidTokenByUser(user1);
        Assertions.assertThat(validTokenByUser).isEmpty(); //조회 실패
        List<RefreshToken> revokedTokens = refreshTokenRepository.findByUser(user1);
        Assertions.assertThat(revokedTokens).allMatch(RefreshToken::isRevoked);
    }
}