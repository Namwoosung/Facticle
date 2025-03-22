package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.entity.Comment;
import com.example.facticle.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
    SELECT c FROM Comment c
    JOIN FETCH c.user
    LEFT JOIN FETCH c.parentComment
    WHERE c.news = :news
    ORDER BY c.createdAt ASC
    """)
    List<Comment> findAllByNewsWithUser(News news);
}
