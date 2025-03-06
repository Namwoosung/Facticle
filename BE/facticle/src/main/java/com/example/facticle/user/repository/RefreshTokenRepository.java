package com.example.facticle.user.repository;

import com.example.facticle.user.entity.RefreshToken;
import com.example.facticle.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    //현재는 비즈니스 요구사항에 맞게 다음과 같이 메서드 설계
    //추후 성능적인 요소를 고려하여 리팩토링
    //(조회 및 update 성능 향상, user기반 CRUD가 많으므로 Index 추가, 너무 많은 refresh token이 쌓이지 않도록 기간이 만료된 refresh token 주기적으로 삭제, ...)

    List<RefreshToken> findByUser(User user);

    /**
     * 특정 사용자의 모든 Refresh Token을 무효화 (RTR 적용)
     */
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken rt SET rt.isRevoked = true WHERE rt.user = :user")
    void revokeAllByUser(User user); //벌크 연산 찾아보고 필요 시 도입

    /**
     * 특정 사용자의 가장 최근에 발급된 유효한 Refresh Token 찾기
     */
    @Query("SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND rt.isRevoked = false AND rt.expiresAt > CURRENT_TIMESTAMP ORDER BY rt.expiresAt DESC")
    Optional<RefreshToken> findValidTokenByUser(User user);
}
