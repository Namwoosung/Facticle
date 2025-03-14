package com.example.facticle.news.controller;

import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsCategory;
import com.example.facticle.news.entity.NewsContent;
import com.example.facticle.news.repository.NewsRepository;
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


    @BeforeAll
    static void setTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @BeforeEach
    void setUp(){
        // 첫 번째 뉴스 데이터
        news1 = News.builder()
                .url("https://news.example.com/article1")
                .naverUrl("https://news.naver.com/article1")
                .title("첫 번째 뉴스 제목")
                .summary("첫 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article1.jpg")
                .mediaName("뉴스미디어1")
                .category(NewsCategory.경제)
                .headlineScore(new BigDecimal("85.50"))
                .factScore(new BigDecimal("90.20"))
                .headlineScoreReason("신뢰할 만한 헤드라인")
                .factScoreReason("검증된 사실 기반")
                .collectedAt(LocalDateTime.now())
                .likeCount(10)
                .hateCount(2)
                .commentCount(5)
                .viewCount(1000)
                .ratingCount(20)
                .rating(new BigDecimal("4.5"))
                .build();

        // 첫 번째 뉴스 콘텐츠 추가
        NewsContent newsContent1 = NewsContent.builder()
                .content("이것은 첫 번째 뉴스의 전체 본문입니다.")
                .news(news1)
                .build();
        news1.addNewsContent(newsContent1);

        // 두 번째 뉴스 데이터
        news2 = News.builder()
                .url("https://news.example.com/article2")
                .naverUrl("https://news.naver.com/article2")
                .title("두 번째 뉴스 제목")
                .summary("두 번째 뉴스의 요약")
                .imageUrl("https://news.example.com/article2.jpg")
                .mediaName("뉴스미디어2")
                .category(NewsCategory.스포츠)
                .headlineScore(new BigDecimal("75.80"))
                .factScore(new BigDecimal("88.40"))
                .headlineScoreReason("흥미로운 헤드라인")
                .factScoreReason("사실 기반 보도")
                .collectedAt(LocalDateTime.now())
                .likeCount(15)
                .hateCount(1)
                .commentCount(8)
                .viewCount(1500)
                .ratingCount(25)
                .rating(new BigDecimal("4.8"))
                .build();

        // 두 번째 뉴스 콘텐츠 추가
        NewsContent newsContent2 = NewsContent.builder()
                .content("이것은 두 번째 뉴스의 전체 본문입니다.")
                .news(news2)
                .build();
        news2.addNewsContent(newsContent2);

        // 데이터 저장
        newsRepository.save(news1);
        newsRepository.save(news2);


        // 강제 플러시 (즉시 DB 반영)
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
}