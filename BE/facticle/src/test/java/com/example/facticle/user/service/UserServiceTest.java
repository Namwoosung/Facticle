package com.example.facticle.user.service;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.common.authority.TokenValidationResult;
import com.example.facticle.common.dto.CustomUserDetails;
import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.common.exception.InvalidTokenException;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.dto.UpdateProfileRequestDto;
import com.example.facticle.user.entity.*;
import com.example.facticle.user.repository.RefreshTokenRepository;
import com.example.facticle.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@SpringBootTest
@ActiveProfiles("test")
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

    private final String BUCKET_NAME = "facticle-profile-images";
    private final String FOLDER_NAME = "profile-images/";
    private final String DEFAULT_PROFILE_IMAGE_URL = "https://facticle-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/default.png";


    @BeforeAll
    static void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        user1 = User.builder()
                .nickname("nick1")
                .localAuth(new LocalAuth("user1", passwordEncoder.encode("userPassword1!")))
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .email("user1@naver.com")
                .build();

        user2 = User.builder()
                .nickname("nick2")
                .localAuth(new LocalAuth("user2", passwordEncoder.encode("userPassword2!")))
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .email("user2@gmail.com")
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
        LocalSignupRequestDto localSignupRequestDto = LocalSignupRequestDto.builder()
                .username("testUser")
                .password("qwerqwer1!")
                .nickname("테스트")
                .build();

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

        LocalSignupRequestDto localSignupRequestDto = LocalSignupRequestDto.builder()
                .username("user1")
                .password("qwerqwer1!")
                .nickname("nick1")
                .build();

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

    @Test
    @DisplayName("프로필 이미지 - 생성, 조회, 삭제")
    void uploadProfileImageSuccessTest() throws Exception {
        //given
        TokenInfo tokenInfo1 = userService.localLogin(new LocalLoginRequestDto("user1", "userPassword1!"));
        User user = userRepository.findByLocalAuthUsername("user1").get();
        MultipartFile file = new MockMultipartFile(
                "profileImage",
                "test1.png",
                "image/png",
                Files.readAllBytes(Path.of(System.getProperty("user.dir") + "/profileImages/" + "test1.png"))
        );

        //when
        Assertions.assertThat(user.getProfileImage()).isEqualTo(DEFAULT_PROFILE_IMAGE_URL);
        String storedFilePath = userService.uploadProfileImage(user.getUserId(), file);
        entityManager.flush();
        entityManager.clear();

        //then
        Assertions.assertThat(storedFilePath).startsWith("https://facticle-profile-images.s3.ap-northeast-2.amazonaws.com/profile-images/");
        User findUser1 = userRepository.findById(user.getUserId()).get();

        Assertions.assertThat(findUser1.getProfileImage()).isEqualTo(storedFilePath);

        userService.deleteProfileImage(user.getUserId());
        entityManager.flush();
        entityManager.clear();
        User findUser2 = userRepository.findById(user.getUserId()).get();
        Assertions.assertThat(findUser2.getProfileImage()).isEqualTo(DEFAULT_PROFILE_IMAGE_URL);
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void updateUserProfileSuccessTest(){
        User user = userRepository.findByLocalAuthUsername("user1").get();


        userService.updateUserProfile(user.getUserId(), new UpdateProfileRequestDto("updatedNickname", "newEmail@naver.com"));
        entityManager.flush();
        entityManager.clear();
        User newUser = userRepository.findByLocalAuthUsername("user1").get();


        Assertions.assertThat(newUser.getNickname()).isEqualTo("updatedNickname");
        Assertions.assertThat(newUser.getEmail()).isEqualTo("newEmail@naver.com");
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패")
    void updateUserProfileFailTest(){
        User user = userRepository.findByLocalAuthUsername("user1").get();

        //닉네임 중복
        Assertions.assertThatThrownBy(() -> userService.updateUserProfile(user.getUserId(), new UpdateProfileRequestDto(user.getNickname(), "newEmail@naver.com")))
                .isInstanceOf(InvalidInputException.class)
                .satisfies(ex -> {
                    InvalidInputException e = (InvalidInputException) ex;
                    Assertions.assertThat(e.getErrors()).containsKey("nickname");
                });

        //이메일 중복
        Assertions.assertThatThrownBy(() -> userService.updateUserProfile(user.getUserId(), new UpdateProfileRequestDto("newNickname", user.getEmail())))
                .isInstanceOf(InvalidInputException.class)
                .satisfies(ex -> {
                    InvalidInputException e = (InvalidInputException) ex;
                    Assertions.assertThat(e.getErrors()).containsKey("email");
                });
    }
}