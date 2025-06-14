package com.example.facticle.news.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(of = {"newsId", "url", "naverUrl", "title", "summary", "imageUrl", "mediaName", "category", "headlineScore", "factScore", "headlineScoreReason", "factScoreReason", "collectedAt"})
@Table(name = "news",
        indexes = {
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_headline_score", columnList = "headlineScore"),
                @Index(name = "idx_fact_score", columnList = "factScore"),
                @Index(name = "idx_collected_at", columnList = "collectedAt"),
                @Index(name = "idx_media_name", columnList = "mediaName")
        },
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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String headlineScoreReason;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String factScoreReason;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime collectedAt;

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

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal totalRatingSum = BigDecimal.valueOf(0.0);

    @JsonIgnore
    @OneToOne(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private NewsContent newsContent;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsInteraction> newsInteractions = new ArrayList<>();
    protected void addNewsInteraction(NewsInteraction newsInteraction){
        this.newsInteractions.add(newsInteraction);
    }

    //연관관계 편의 메서드
    //사실 뉴스 저장은 모두 크롤링 서버에서 담당하므로 호출될 일은 없을 것으료 예상되지만, 추후 확장성을 고려해 작성해둠
    public void addNewsContent(NewsContent newsContent){
        this.newsContent = newsContent;
        newsContent.setNews(this);
    }

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount--;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount--;
    }

    public void increaseHateCount() {
        this.hateCount++;
    }

    public void decreaseHateCount() {
        this.hateCount--;
    }

    public void increaseRatingCount() {
        this.ratingCount++;
    }

    public void decreaseRatingCount() {
        this.ratingCount--;
    }

    public void increaseRating(BigDecimal rating){
        this.totalRatingSum =  this.totalRatingSum.add(rating);
    }

    public void decreaseRating(BigDecimal rating){
        this.totalRatingSum =  this.totalRatingSum.subtract(rating);
    }

    public BigDecimal getAverageRating() {
        return ratingCount == 0 ? BigDecimal.ZERO :
                totalRatingSum.divide(BigDecimal.valueOf(ratingCount), 1, RoundingMode.HALF_UP);
    }
}
