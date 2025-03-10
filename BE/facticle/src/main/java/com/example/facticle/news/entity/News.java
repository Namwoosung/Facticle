package com.example.facticle.news.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "news",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "url")
        }
)
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column(nullable = false)
    private String url;

    private String naverUrl;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    private String imageUrl;

    private String mediaName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NewsCategory category;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal headlineScore;  // 헤드라인 신뢰도 점수 (0~100)

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal factScore;  // 팩트 신뢰도 점수 (0~100)

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    @Builder.Default
    private LocalDateTime collectedAt = LocalDateTime.now();

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int hateCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int commentCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int viewCount = 0;
    @Column(nullable = false)
    @Builder.Default
    private int ratingCount = 0;

    @Column(precision = 2, scale = 1)
    @Builder.Default
    private BigDecimal rating = BigDecimal.valueOf(0.0);

    @OneToOne(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private NewsContent newsContent;
}
