package com.example.facticle.news.entity;

import com.example.facticle.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

//일대다로 매핑 수정 필요

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString(of = {"commentInteractionId", "reaction", "reactionAt"})
@Table(name = "comment_interactions",
        indexes = {
                @Index(name = "idx_comment_interactions_user_id", columnList = "user_id"),
                @Index(name = "idx_comment_interactions_comment_id", columnList = "comment_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "unique_user_comment", columnNames = {"comment_id", "user_id"})
        }
)
public class CommentInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentInteractionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    private ReactionType reaction;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime reactionAt;

    public void updateUser(User user){
        this.user = user;
        user.setCommentInteraction(this);
    }

    public void updateComment(Comment comment){
        this.comment = comment;
        comment.setCommentInteraction(this);
    }

    public void updateReaction(ReactionType reaction, LocalDateTime reactionAt){
        this.reaction = reaction;
        this. reactionAt = reactionAt;
    }
}
