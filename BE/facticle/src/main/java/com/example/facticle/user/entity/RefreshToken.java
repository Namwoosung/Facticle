package com.example.facticle.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(of = {"tokenId", "hashedRefreshToken", "isRevoked", "issuedAt", "expiresAt"})
@Table(name = "refresh_tokens",
    indexes = {
            @Index(name = "idx_user_id", columnList = "user_id"),
            @Index(name = "idx_expires_at", columnList = "expiresAt")
    }
)
public class RefreshToken {
    //추후 hashedRefreshToken에 따른 성능을 측정해보고 문제가 된다면, 토큰의 UUID 값을 활용하도록 리팩토링 고려

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String hashedRefreshToken; //refresh token은 노출될 경우 큰 보안문제 발생 가능, hash화 하여 저장

    @Builder.Default
    private boolean isRevoked = false;

    @Column(updatable = false, columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime issuedAt; //token의 발행시간과 동일하게 설정

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime expiresAt; //token의 만료시간과 동일하게 설정

    public void revoke(){
        this.isRevoked = true;
    }

    public boolean isValid(){
        return !isRevoked && LocalDateTime.now().isBefore(expiresAt);
    }

    protected void setUser(User user) {
        this.user = user;
    }
}
