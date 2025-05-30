package com.example.facticle.news.entity;

import com.example.facticle.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(of = {"commentId", "content", "likeCount", "hateCount", "createdAt", "updatedAt"})
@Table(name = "comments",
    indexes = {
        @Index(name = "idx_comments_news_id", columnList = "news_id"),
        @Index(name = "idx_comments_user_id", columnList = "user_id")
    }
)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id", nullable = false)
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    @Builder.Default
    private int likeCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private int hateCount = 0;


    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime createdAt;

    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime updatedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    //연관관계 편의 메서드
    public void addReplies(Comment comment){
        replies.add(comment);
        comment.parentComment = this;
    }

    @JsonIgnore
    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentInteraction> commentInteractions = new ArrayList<>();
    protected void addCommentInteraction(CommentInteraction commentInteraction){ //사용x
        this.commentInteractions.add(commentInteraction);
    }

    //연관관계 편의 메서드
    public void updateUser(User user){
        this.user = user;
        user.getComments().add(this);
    }

    //연관관계 편의 메서드
    public void updateNews(News news){
        this.news = news;
        news.getComments().add(this);
    }

    public void updateContent(String content, LocalDateTime updatedAt){
        this.content = content;
        this.updatedAt = updatedAt;
    }

    protected void setParentComment(Comment comment){
        this.parentComment = comment;
    } //사용 x

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
}
