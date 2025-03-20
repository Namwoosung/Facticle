package com.example.facticle.news.repository.elasticsearch;

import com.example.facticle.news.entity.NewsDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsDocumentRepository extends ElasticsearchRepository<NewsDocument, String>, NewsDocumentRepositoryCustom {
}
