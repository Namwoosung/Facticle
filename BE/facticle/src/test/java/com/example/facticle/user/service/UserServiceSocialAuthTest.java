package com.example.facticle.user.service;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.exception.OAuthException;
import com.example.facticle.user.dto.SocialLoginRequestDto;
import com.example.facticle.user.dto.SocialLoginResponseDto;
import com.example.facticle.user.entity.*;
import com.example.facticle.user.oauth.SocialAuthProvider;
import com.example.facticle.user.oauth.SocialAuthProviderFactory;
import com.example.facticle.user.repository.RefreshTokenRepository;
import com.example.facticle.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserServiceSocialAuthTest {
    /*
    대부분의 테스트 코드는 통합 테스트로서, 실제 구성요소들을 활용하여 테스트를 진행
    그러나 OAuth의 경우 실제 social api를 활용해서 authorization code를 가져오는 것이 매우 번거로움
    => OAuth 관련 로직들은 Mock 객체를 활용해 단위 테스트로서 진행
    추후 프론트와 통합 테스트를 구성할 때, 실제 소셜 api에 접근하면서 테스트를 진행
     */

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private SocialAuthProviderFactory socialAuthProviderFactory;

    @Mock
    private SocialAuthProvider socialAuthProvider;

    private SocialLoginRequestDto socialLoginRequestDto;
    private SocialUserInfo mockSocialUserInfo;
    private User mockUser;

    @BeforeAll
    static void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setUp() {
        // 소셜 로그인 요청 DTO
        socialLoginRequestDto = new SocialLoginRequestDto("google", "valid_auth_code");

        // Mock 소셜 유저 정보 (Google로부터 받아온 사용자 정보)
        mockSocialUserInfo = SocialUserInfo.builder()
                .socialId("123456789")
                .email("testuser@gmail.com")
                .provider("google")
                .build();

        // Mock 사용자 (기존 사용자)
        mockUser = User.builder()
                .userId(1L)
                .nickname("test_nickname")
                .socialAuth(new SocialAuth("google", "_123456789"))
                .email("testuser@gmail.com")
                .role(UserRole.USER)
                .signupType(SignupType.SOCIAL)
                .build();
    }

    @Test
    @DisplayName("소셜 로그인 - 기존 유저 로그인 성공")
    void socialLoginExistingUserTest() {
        // Given: Mock 설정
        when(socialAuthProviderFactory.getAuthProvider("google")).thenReturn(socialAuthProvider);
        when(socialAuthProvider.getUserInfo("valid_auth_code")).thenReturn(mockSocialUserInfo);
        when(userRepository.findBySocialAuthSocialIdAndSocialAuthSocialProvider(
                mockSocialUserInfo.getSocialId(), mockSocialUserInfo.getProvider()))
                .thenReturn(Optional.of(mockUser));

        // JWT 토큰 발급
        when(jwtTokenProvider.createAccessToken(any())).thenReturn("mock_access_token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("mock_refresh_token");

        // When: 소셜 로그인 요청
        SocialLoginResponseDto response = userService.socialLogin(socialLoginRequestDto);

        // Then: 검증
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getTokenInfo().getAccessToken()).isEqualTo("mock_access_token");
        Assertions.assertThat(response.isNew()).isFalse(); // 기존 유저이므로 isNew = false

        // Verify: 특정 메서드가 호출되었는지 확인
        verify(userRepository, never()).save(any()); // 기존 유저이므로 save 호출 X
        verify(refreshTokenRepository).revokeAllByUser(mockUser); // 기존 refresh token 삭제됨
    }

    @Test
    @DisplayName("소셜 로그인 - 신규 유저 회원가입 및 토큰 발급 성공")
    void socialLoginNewUserTest() {
        // Given: Mock 설정 (신규 유저)
        when(socialAuthProviderFactory.getAuthProvider("google")).thenReturn(socialAuthProvider);
        when(socialAuthProvider.getUserInfo("valid_auth_code")).thenReturn(mockSocialUserInfo);
        when(userRepository.findBySocialAuthSocialIdAndSocialAuthSocialProvider(
                mockSocialUserInfo.getSocialId(), mockSocialUserInfo.getProvider()))
                .thenReturn(Optional.empty()); // 신규 유저이므로 DB에 없음

        // 신규 유저 저장 Mock
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            return User.builder()
                    .userId(2L)
                    .nickname(savedUser.getNickname())
                    .socialAuth(savedUser.getSocialAuth())
                    .email(savedUser.getEmail())
                    .role(savedUser.getRole())
                    .signupType(savedUser.getSignupType())
                    .build();
        });

        // JWT 토큰 발급
        when(jwtTokenProvider.createAccessToken(any())).thenReturn("mock_access_token");
        when(jwtTokenProvider.createRefreshToken(any())).thenReturn("mock_refresh_token");

        // When: 소셜 로그인 요청
        SocialLoginResponseDto response = userService.socialLogin(socialLoginRequestDto);

        // Then: 검증
        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getTokenInfo().getAccessToken()).isEqualTo("mock_access_token");
        Assertions.assertThat(response.isNew()).isTrue(); // 신규 유저이므로 isNew = true

        // Verify: 특정 메서드가 호출되었는지 확인
        verify(userRepository).save(any()); // 신규 유저는 저장됨
    }

    @Test
    @DisplayName("소셜 로그인 - OAuth 인증 실패 시 예외 발생")
    void socialLoginOAuthFailureTest() {
        // Given: Mock 설정 (잘못된 인증 코드)
        when(socialAuthProviderFactory.getAuthProvider("google")).thenReturn(socialAuthProvider);
        when(socialAuthProvider.getUserInfo("invalid_auth_code"))
                .thenThrow(new OAuthException("Invalid OAuth Code"));

        // When & Then: 예외 발생 검증
        try {
            userService.socialLogin(new SocialLoginRequestDto("google", "invalid_auth_code"));
        } catch (OAuthException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Invalid OAuth Code");
        }

        // Verify: 유저 저장 로직 실행되지 않음
        verify(userRepository, never()).save(any());
    }
}
