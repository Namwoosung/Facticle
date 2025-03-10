package com.example.facticle.news.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "news_content")
public class NewsContent {
    @Id
    @Column(name = "news_id")
    private Long newsId;  // news의 기본 키를 그대로 사용

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToOne
    @MapsId //newsId가 PK이면서 FK가 되도록 설정
    @JoinColumn(name = "news_id")
    private News news;

}
