package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long>, NewsRepositoryCustom {
    Optional<News> findByUrl(String url);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE News n SET n.viewCount = n.viewCount + 1 WHERE n.newsId = :newsId")
    void incrementViewCount(Long newsId);
}
