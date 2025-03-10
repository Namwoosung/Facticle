package com.example.facticle.common.config;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.user.entity.*;
import com.example.facticle.user.repository.RefreshTokenRepository;
import com.example.facticle.user.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Configuration
@Slf4j
@Profile("!test") //테스트 코드에는 적용 x
public class DataInitializer {
    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @PostConstruct
    public void init(){
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository) {
        return args -> {
            if (userRepository.count() == 0) { // 데이터가 없을 때만 추가
                User user1 = User.builder()
                        .nickname("testUser1")
                        .localAuth(new LocalAuth("user1", passwordEncoder.encode("password1!")))
                        .role(UserRole.USER)
                        .email("testUser1@gmail.com")
                        .signupType(SignupType.LOCAL)
                        .build();

                User user2 = User.builder()
                        .nickname("testUser2")
                        .localAuth(new LocalAuth("user2", passwordEncoder.encode("password2!")))
                        .role(UserRole.USER)
                        .email("testUser2@naver.com")
                        .signupType(SignupType.LOCAL)
                        .build();

                userRepository.saveAll(List.of(user1, user2));


                // 토큰을 고정되게 생성하기 위해 고정된 발급시간을 지정
                long fixedIssuedAtMillis = Instant.parse("2025-02-13T00:00:00Z").toEpochMilli();
                long accessTokenValidity = 1000L * 60 * 60 * 24 * 30; // 30일
                long refreshTokenValidity = 1000L * 60 * 60 * 24 * 60; // 60일

                Date fixedIssuedAt = new Date(fixedIssuedAtMillis);
                Date accessExpiration = new Date(fixedIssuedAtMillis + accessTokenValidity);
                Date refreshExpiration = new Date(fixedIssuedAtMillis + refreshTokenValidity);

                String user1AccessTokenJwt = Jwts.builder()
                        .setSubject("1")
                        .claim("auth", "ROLE_" + user1.getRole().name())
                        .claim("username", user1.getLocalAuth().getUsername())
                        .claim("userId", user1.getUserId())
                        .claim("tokenType", "ACCESS")
                        .setIssuedAt(fixedIssuedAt)
                        .setExpiration(accessExpiration)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

                //JWT 발급
                String user1RefreshTokenJwt = Jwts.builder()
                        .setSubject("1")
                        .claim("userId", user1.getUserId())
                        .claim("username", user1.getLocalAuth().getUsername())
                        .claim("tokenType", "REFRESH")
                        .setIssuedAt(fixedIssuedAt)
                        .setExpiration(refreshExpiration)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

                RefreshToken user1RefreshToken = RefreshToken.builder()
                        .issuedAt(jwtTokenProvider.getIssuedAt(user1RefreshTokenJwt))
                        .hashedRefreshToken(passwordEncoder.encode(user1RefreshTokenJwt))
                        .isRevoked(false)
                        .user(user1)
                        .expiresAt(jwtTokenProvider.getExpiresAt(user1RefreshTokenJwt))
                        .build();

                user1.addRefreshToken(user1RefreshToken);

                String user2AccessTokenJwt = Jwts.builder()
                        .setSubject("2")
                        .claim("auth", "ROLE_" + user2.getRole().name())
                        .claim("username", user2.getLocalAuth().getUsername())
                        .claim("userId", user2.getUserId())
                        .claim("tokenType", "ACCESS")
                        .setIssuedAt(fixedIssuedAt)
                        .setExpiration(accessExpiration)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

                //JWT 발급
                String user2RefreshTokenJwt = Jwts.builder()
                        .setSubject("2")
                        .claim("userId", user2.getUserId())
                        .claim("username", user2.getLocalAuth().getUsername())
                        .claim("tokenType", "REFRESH")
                        .setIssuedAt(fixedIssuedAt)
                        .setExpiration(refreshExpiration)
                        .signWith(key, SignatureAlgorithm.HS256)
                        .compact();

                RefreshToken user2RefreshToken = RefreshToken.builder()
                        .issuedAt(jwtTokenProvider.getIssuedAt(user2RefreshTokenJwt))
                        .hashedRefreshToken(passwordEncoder.encode(user2RefreshTokenJwt))
                        .isRevoked(false)
                        .user(user2)
                        .expiresAt(jwtTokenProvider.getExpiresAt(user2RefreshTokenJwt))
                        .build();

                user2.addRefreshToken(user2RefreshToken);

                refreshTokenRepository.saveAll(List.of(user1RefreshToken, user2RefreshToken));

                log.debug("user1 AccessToken: {}", user1AccessTokenJwt);
                log.debug("user2 AccessToken: {}", user2AccessTokenJwt);

            } else {
                log.debug("[warn] Test User already exists. do not add new Test User");
            }
        };
    }
}