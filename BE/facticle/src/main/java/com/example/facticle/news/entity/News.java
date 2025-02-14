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
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String context;

    @Column(columnDefinition = "TEXT")
    private String summarizedText;

    @Column(nullable = false)
    private String url;

    private String imageUrl;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime publishedAt;

    private String mediaName;

    private String reporterName;

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
    private BigDecimal rating;

    @Column(precision = 5, scale = 2)
    private BigDecimal similarityScore;

    @Column(columnDefinition = "TEXT")
    private String similarityInfo;
}
