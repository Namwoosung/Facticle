package com.example.facticle.news.repository;

import com.example.facticle.news.dto.NewsSearchCondition;
import com.example.facticle.news.dto.SortBy;
import com.example.facticle.news.dto.SortDirection;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsCategory;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsRepositoryTest {

    @Autowired
    NewsRepository newsRepository;
    @Autowired
    EntityManager entityManager;


    private News news1;
    private News news2;
    private News news3;
    private News news4;
    private News news5;



    @BeforeAll
    static void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setUp(){
        news1 = News.builder()
                .url("https://news.example.com/article1")
                .naverUrl("https://news.naver.com/article1")
                .title("첫 번째 뉴스 제목")
                .summary("첫 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article1.jpg")
                .mediaName("뉴스미디어1")
                .category(NewsCategory.ECONOMY)
                .headlineScore(new BigDecimal("85.50"))
                .factScore(new BigDecimal("90.20"))
                .collectedAt(LocalDateTime.now().minusDays(2))
                .likeCount(10)
                .hateCount(100)
                .viewCount(1000)
                .ratingCount(20)
                .rating(new BigDecimal("4.5"))
                .factScoreReason("fs 이유")
                .headlineScoreReason("hs 이유")
                .build();

        news2 = News.builder()
                .url("https://news.example.com/article2")
                .naverUrl("https://news.naver.com/article2")
                .title("두 번째 뉴스 제목")
                .summary("두 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article2.jpg")
                .mediaName("뉴스미디어2")
                .category(NewsCategory.ENTERTAINMENT)
                .headlineScore(new BigDecimal("78.40"))
                .factScore(new BigDecimal("85.60"))
                .collectedAt(LocalDateTime.now().minusDays(5))
                .likeCount(250)
                .hateCount(50)
                .viewCount(7000)
                .ratingCount(30)
                .rating(new BigDecimal("4.3"))
                .factScoreReason("fs 이유")
                .headlineScoreReason("hs 이유")
                .build();


        news3 = News.builder()
                .url("https://news.example.com/article3")
                .naverUrl("https://news.naver.com/article3")
                .title("세 번째 뉴스 제목")
                .summary("세 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article3.jpg")
                .mediaName("뉴스미디어3")
                .category(NewsCategory.TECH)
                .headlineScore(new BigDecimal("40.75"))
                .factScore(new BigDecimal("70.10"))
                .collectedAt(LocalDateTime.now().minusDays(10))
                .likeCount(5)
                .hateCount(200)
                .viewCount(500)
                .ratingCount(5)
                .rating(new BigDecimal("2.1"))
                .factScoreReason("fs 이유")
                .headlineScoreReason("hs 이유")
                .build();

        news4 = News.builder()
                .url("https://news.example.com/article4")
                .naverUrl("https://news.naver.com/article4")
                .title("네 번째 뉴스 제목")
                .summary("네 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article4.jpg")
                .mediaName("뉴스미디어4")
                .category(NewsCategory.SPORTS)
                .headlineScore(new BigDecimal("95.00"))
                .factScore(new BigDecimal("98.90"))
                .collectedAt(LocalDateTime.now().minusDays(1))
                .likeCount(500)
                .hateCount(5)
                .viewCount(10000)
                .ratingCount(50)
                .rating(new BigDecimal("5.0"))
                .factScoreReason("fs 이유")
                .headlineScoreReason("hs 이유")
                .build();

        news5 = News.builder()
                .url("https://news.example.com/article5")
                .naverUrl("https://news.naver.com/article5")
                .title("다섯 번째 뉴스 제목")
                .summary("다섯 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article5.jpg")
                .mediaName("뉴스미디어5")
                .category(NewsCategory.POLITICS)
                .headlineScore(new BigDecimal("20.00"))
                .factScore(new BigDecimal("30.50"))
                .collectedAt(LocalDateTime.now().minusDays(20))
                .likeCount(1)
                .hateCount(500)
                .viewCount(200)
                .ratingCount(2)
                .rating(new BigDecimal("1.0"))
                .factScoreReason("fs 이유")
                .headlineScoreReason("hs 이유")
                .build();


        newsRepository.save(news1);
        newsRepository.save(news2);
        newsRepository.save(news3);
        newsRepository.save(news4);
        newsRepository.save(news5);

        entityManager.flush();
    }

    @Test
    @DisplayName("뉴스 리스트 검색 테스트")
    void newsSearchListTest() {
        // 카테고리 필터링 테스트
        NewsSearchCondition categoryCondition = NewsSearchCondition.builder()
                .categories(List.of(NewsCategory.SPORTS, NewsCategory.ECONOMY))
                .build();
        List<News> categoryResults = newsRepository.searchNewsList(categoryCondition);
        Assertions.assertThat(categoryResults).hasSize(2);

        // 언론사 필터링 테스트
        NewsSearchCondition publisherCondition = NewsSearchCondition.builder()
                .publishers(List.of("뉴스미디어1", "뉴스미디어3"))
                .build();
        List<News> publisherResults = newsRepository.searchNewsList(publisherCondition);
        Assertions.assertThat(publisherResults).hasSize(2);

        // 조회수 필터링 테스트
        NewsSearchCondition viewCountCondition = NewsSearchCondition.builder()
                .minViews(500)
                .maxViews(7000)
                .build();
        List<News> viewCountResults = newsRepository.searchNewsList(viewCountCondition);
        Assertions.assertThat(viewCountResults).hasSize(3);

        // 좋아요 필터링 테스트
        NewsSearchCondition likesCondition = NewsSearchCondition.builder()
                .minLikes(10)
                .maxLikes(250)
                .build();
        List<News> likesResults = newsRepository.searchNewsList(likesCondition);
        Assertions.assertThat(likesResults).hasSize(2);

        // 싫어요 필터링 테스트
        NewsSearchCondition dislikesCondition = NewsSearchCondition.builder()
                .minDislikes(50)
                .maxDislikes(200)
                .build();
        List<News> dislikesResults = newsRepository.searchNewsList(dislikesCondition);
        Assertions.assertThat(dislikesResults).hasSize(3);


        // 평점 필터링 테스트
        NewsSearchCondition ratingCondition = NewsSearchCondition.builder()
                .minRating(new BigDecimal("2.0"))
                .maxRating(new BigDecimal("4.5"))
                .build();
        List<News> ratingResults = newsRepository.searchNewsList(ratingCondition);
        Assertions.assertThat(ratingResults).hasSize(3);

        // 수집 날짜 필터링 테스트
        NewsSearchCondition collectedAtCondition = NewsSearchCondition.builder()
                .startDate(LocalDateTime.now().minusDays(10))
                .endDate(LocalDateTime.now())
                .build();
        List<News> collectedAtResults = newsRepository.searchNewsList(collectedAtCondition);
        Assertions.assertThat(collectedAtResults).hasSize(4);

        // 정렬 테스트 (조회수 내림차순)
        NewsSearchCondition sortCondition = NewsSearchCondition.builder()
                .sortBy(SortBy.VIEW_COUNT)
                .sortDirection(SortDirection.DESC)
                .build();
        List<News> sortedResults = newsRepository.searchNewsList(sortCondition);
        Assertions.assertThat(sortedResults.get(0).getViewCount()).isGreaterThanOrEqualTo(sortedResults.get(1).getViewCount());

        // 페이징 테스트 (size=3, page=1)
        NewsSearchCondition pagingCondition = NewsSearchCondition.builder()
                .page(1)
                .size(3)
                .build();
        List<News> pagedResults = newsRepository.searchNewsList(pagingCondition);
        Assertions.assertThat(pagedResults).hasSize(2);
    }
}