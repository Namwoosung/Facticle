package com.example.facticle.news.controller;

import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsCategory;
import com.example.facticle.news.repository.jpa.NewsRepository;
import com.example.facticle.news.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class NewsControllerTest {
    @Autowired
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper(); //Json 직렬화

    @Autowired
    NewsController newsController;

    @Autowired
    NewsService newsService;

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
    @DisplayName("개별 뉴스 조회 테스트 - 성공, 실패 모두")
    void getNewsTest() throws Exception {

        News news = newsRepository.findById(news1.getNewsId()).get();


        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/news/%d", news.getNewsId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("news retrieved successfully."));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/news/-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid input"));

    }

    @Test
    @DisplayName("전체 뉴스 조회 성공")
    void getNewsListTestSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(String.format("/api/news/search"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Search results retrieved successfully."));
    }

    @Test
    @DisplayName("뉴스 검색 조건 검증 테스트 - 쿼리 파라미터 검증 실패 케이스")
    void getNewsListTesFailConditionValidation() throws Exception {
        // 1. 잘못된 page 값 (음수)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/news/search")
                        .param("page", "-1") // 유효하지 않은 page 값
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

        // 2. 잘못된 size 값 (최소값보다 작음)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/news/search")
                        .param("size", "0") // 최소 1 이상이어야 함
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

        // 3. 잘못된 size 값 (최대값보다 큼)
        mockMvc.perform(MockMvcRequestBuilders.get("/api/news/search")
                        .param("size", "101") // 최대 100 이하이어야 함
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증

        // 4. 시작 날짜가 종료 날짜보다 이후일 경우
        mockMvc.perform(MockMvcRequestBuilders.get("/api/news/search")
                        .param("startDate", "2025-03-10T10:00:00")
                        .param("endDate", "2025-03-09T10:00:00") // startDate > endDate 오류
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()) // HTTP 400 응답을 기대
                .andExpect(jsonPath("$.message").value("Validation failed.")) // 예외 메시지 검증
                .andExpect(jsonPath("$.data.code").value(400)); // 응답 코드 검증
    }
}