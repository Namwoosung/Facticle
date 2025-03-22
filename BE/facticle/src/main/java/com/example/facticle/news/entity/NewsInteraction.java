package com.example.facticle.news.entity;

import com.example.facticle.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(of = {"newsInteractionId", "reaction", "rating", "reactionAt", "ratedAt", "viewedAt"})
@Table(name = "news_interactions",
        indexes = {
                @Index(name = "idx_news_interactions_user_id", columnList = "user_id"),
                @Index(name = "idx_news_interactions_news_id", columnList = "news_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_news", columnNames = {"news_id", "user_id"})
        }
)
public class NewsInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsInteractionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @Enumerated(EnumType.STRING)
    private ReactionType reaction;

    @Column(precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime reactionAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime ratedAt;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime viewedAt;

    //연관관계 편의 메서드
    public void updateUser(User user){
        this.user = user;
        user.setNewsInteraction(this);
    }

    //연관관계 편의 메서드
    public void updateNews(News news){
        this.news = news;
        news.setNewsInteraction(this);
    }

    //리액션 수정
    public void updateReaction(ReactionType reaction, LocalDateTime reactionAt){
        this.reaction = reaction;
        this.reactionAt = reactionAt;
    }

    public void updateViewedAt(LocalDateTime viewedAt){
        this.viewedAt =viewedAt;
    }
}
