package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsInteraction;
import com.example.facticle.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsInteractionRepository extends JpaRepository<NewsInteraction, Long> {
    Optional<NewsInteraction> findByUserAndNews(User user, News news);
}
