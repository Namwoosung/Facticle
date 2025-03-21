package com.example.facticle.news.repository.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.facticle.news.entity.NewsDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class NewsDocumentRepositoryCustomImpl implements NewsDocumentRepositoryCustom{

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public List<Long> searchByTitle(List<String> keywords) {
        try {
            // must 조건을 적용한 쿼리 생성
            List<Query> mustQueries = keywords.stream()
                    .map(keyword -> Query.of(q -> q.matchPhrase(m -> m
                            .field("title")
                            .query(keyword))))
                    .collect(Collectors.toList());

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("news_index")
                    .query(q -> q.bool(b -> b.must(mustQueries))) // 모든 키워드를 포함하는 must 조건 적용
                    .build();

            SearchResponse<NewsDocument> response = elasticsearchClient.search(searchRequest, NewsDocument.class);

            log.info("response {}", response);

            return response.hits().hits().stream()
                    .map(hit -> {
                        log.info("hit {}", hit);
                        String newsId = hit.id(); //id만 가져옴
                        return convertToLong(newsId);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 오류", e);
        }
    }

    @Override
    public List<Long> searchByContent(List<String> keywords) {
        try {
            // must 조건을 적용한 쿼리 생성
            List<Query> mustQueries = keywords.stream()
                    .map(keyword -> Query.of(q -> q.matchPhrase(m -> m
                            .field("content")
                            .query(keyword))))
                    .collect(Collectors.toList());

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("news_index")
                    .query(q -> q.bool(b -> b.must(mustQueries))) // 모든 키워드를 포함하는 must 조건 적용
                    .build();

            SearchResponse<NewsDocument> response = elasticsearchClient.search(searchRequest, NewsDocument.class);

            log.info("response {}", response);

            return response.hits().hits().stream()
                    .map(hit -> {
                        log.info("hit {}", hit);
                        String newsId = hit.id(); //id만 가져옴
                        return convertToLong(newsId);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 오류", e);
        }
    }

    @Override
    public List<Long> searchByTitleOrContent(List<String> keywords) {
        try {
            // must 조건 내에서 should 조건을 적용하여 OR 검색 수행
            List<Query> mustQueries = keywords.stream()
                    .map(keyword -> {
                        List<Query> shouldQueries = List.of(
                                Query.of(q -> q.matchPhrase(m -> m.field("title").query(keyword))),
                                Query.of(q -> q.matchPhrase(m -> m.field("content").query(keyword)))
                        );

                        return Query.of(q -> q.bool(innerBool -> innerBool.should(shouldQueries)));
                    })
                    .collect(Collectors.toList());

            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("news_index")
                    .query(q -> q.bool(b -> b.must(mustQueries)))
                    .build();

            SearchResponse<NewsDocument> response = elasticsearchClient.search(searchRequest, NewsDocument.class);

            log.info("response {}", response);

            return response.hits().hits().stream()
                    .map(hit -> {
                        log.info("hit {}", hit);
                        String newsId = hit.id(); //id만 가져옴
                        return convertToLong(newsId);
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch 검색 오류", e);
        }
    }

    private Long convertToLong(String newsId) {
        try {
            return newsId != null ? Long.parseLong(newsId) : null;
        } catch (NumberFormatException e) {
            return null; // 변환 실패 시 null 반환 (예외 방지)
        }
    }
}
