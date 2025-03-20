package com.example.facticle.news.repository.elasticsearch;

import java.util.List;

public interface NewsDocumentRepositoryCustom {
    // 제목에서 검색 (모든 키워드가 포함된 뉴스 ID 반환)
    List<Long> searchByTitle(List<String> keywords);

    // 본문에서 검색 (모든 키워드가 포함된 뉴스 ID 반환)
    List<Long> searchByContent(List<String> keywords);

    // 제목 또는 본문에서 검색 (모든 키워드가 포함된 뉴스 ID 반환)
    List<Long> searchByTitleOrContent(List<String> keywords);
}
