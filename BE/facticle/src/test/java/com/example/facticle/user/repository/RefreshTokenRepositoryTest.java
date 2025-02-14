package com.example.facticle.user.repository;

import com.example.facticle.user.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RefreshTokenRepositoryTest {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;


    private User testUser;
    private RefreshToken validToken;
    private RefreshToken expiredToken;
    private RefreshToken revokedToken;
    private LocalAuth localAuth;

    @BeforeEach
    void setUp() {
        localAuth = new LocalAuth("user1", passwordEncoder.encode("testPassword1!"));
        // 테스트용 User 생성 및 저장
        testUser = User.builder()
                .nickname("nick1")
                .localAuth(localAuth)
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();
        userRepository.save(testUser);

        // 유효한 Refresh Token 저장
        validToken = RefreshToken.builder()
                .user(testUser)
                .hashedRefreshToken(passwordEncoder.encode("valid_token_hash"))
                .isRevoked(false)
                .issuedAt(LocalDateTime.now().minusMinutes(5))
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // 30분 후 만료
                .build();
        refreshTokenRepository.save(validToken);

        // 만료된 Refresh Token
        expiredToken = RefreshToken.builder()
                .user(testUser)
                .hashedRefreshToken(passwordEncoder.encode("expired_token_hash"))
                .isRevoked(false)
                .issuedAt(LocalDateTime.now().minusMinutes(40))
                .expiresAt(LocalDateTime.now().minusMinutes(10)) // 이미 만료됨
                .build();
        refreshTokenRepository.save(expiredToken);

        // Revoke 된 Refresh Token
        revokedToken = RefreshToken.builder()
                .user(testUser)
                .hashedRefreshToken(passwordEncoder.encode("revoked_token_hash"))
                .isRevoked(true) // 취소됨
                .issuedAt(LocalDateTime.now().minusMinutes(10))
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();
        refreshTokenRepository.save(revokedToken);
    }

    @Test
    @DisplayName("유효한 refresh token 조회")
    void findValidTokenByUserTest(){
        Optional<RefreshToken> validTokenByUser = refreshTokenRepository.findValidTokenByUser(testUser);

        Assertions.assertThat(validTokenByUser).isPresent();
        Assertions.assertThat(validTokenByUser.get().getTokenId()).isEqualTo(validToken.getTokenId());
    }

    @Test
    @DisplayName("모든 refresh token revoke 테스트")
    void revokeAllByUserTest(){
        refreshTokenRepository.revokeAllByUser(testUser);

        Optional<RefreshToken> activeToken = refreshTokenRepository.findValidTokenByUser(testUser);
        Assertions.assertThat(activeToken).isEmpty();
    }

}