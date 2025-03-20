package com.example.facticle.news.repository.jpa;

import com.example.facticle.news.dto.NewsListResponseDto;
import com.example.facticle.news.dto.NewsSearchCondition;
import com.example.facticle.news.entity.News;

import java.util.List;

public interface NewsRepositoryCustom {


    List<News> searchNewsList(NewsSearchCondition condition);
}
