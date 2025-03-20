package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.entity.NewsContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsContentRepository extends JpaRepository<NewsContent, Long> {
}
