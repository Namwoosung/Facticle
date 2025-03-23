package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.entity.Comment;
import com.example.facticle.news.entity.CommentInteraction;
import com.example.facticle.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentInteractionRepository extends JpaRepository<CommentInteraction, Long> {
    @Query("SELECT ci FROM CommentInteraction ci WHERE ci.user = :user AND ci.comment IN :comments")
    List<CommentInteraction> findAllByUserAndComments(User user, List<Comment> comments);

    Optional<CommentInteraction> findByUserAndComment(User user, Comment comment);

}
