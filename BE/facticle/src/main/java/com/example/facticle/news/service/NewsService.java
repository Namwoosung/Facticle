package com.example.facticle.news.service;

import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.news.dto.NewsSearchCondition;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsInteraction;
import com.example.facticle.news.entity.ReactionType;
import com.example.facticle.news.repository.jpa.NewsInteractionRepository;
import com.example.facticle.news.repository.jpa.NewsRepository;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsInteractionRepository newsInteractionRepository;
    private final UserRepository userRepository;

    public News getNews(Long newsId) {
        //조회 수 관련 로직 추가

        //DTO를 추가해서 댓글정보도 함께 전달
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("news not found", Map.of("newsId", "newsId does not exist.")));
    }

    @Transactional(readOnly = true)
    public List<News> getNewsList(NewsSearchCondition condition){
        return newsRepository.searchNewsList(condition);
    }


    public void likeNews(Long newsId, Long userId) {
        NewsInteraction newsInteraction = getExistingNewsInteraction(newsId, userId);

        if(newsInteraction.getReaction() == ReactionType.LIKE){
            throw new InvalidInputException("reaction is not available", Map.of("ReactionType" ,"user's ReactionType is already Like"));
        }

        newsInteraction.updateReaction(ReactionType.LIKE, LocalDateTime.now());
    }

    public void unlikeNews(Long newsId, Long userId) {
        NewsInteraction newsInteraction = getExistingNewsInteraction(newsId, userId);

        if(newsInteraction.getReaction() != ReactionType.LIKE){
            throw new InvalidInputException("reaction is not available", Map.of("ReactionType" ,"user's ReactionType is not Like"));
        }

        newsInteraction.updateReaction(null, null);
    }

    public void hateNews(Long newsId, Long userId) {
        NewsInteraction newsInteraction = getExistingNewsInteraction(newsId, userId);

        if(newsInteraction.getReaction() == ReactionType.HATE){
            throw new InvalidInputException("reaction is not available", Map.of("ReactionType" ,"user's ReactionType is already hate"));
        }

        newsInteraction.updateReaction(ReactionType.HATE, LocalDateTime.now());
    }

    public void unhateNews(Long newsId, Long userId) {
        NewsInteraction newsInteraction = getExistingNewsInteraction(newsId, userId);

        if(newsInteraction.getReaction() != ReactionType.HATE){
            throw new InvalidInputException("reaction is not available", Map.of("ReactionType" ,"user's ReactionType is not hate"));
        }

        newsInteraction.updateReaction(null, null);
    }





    private NewsInteraction getExistingNewsInteraction(Long newsId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidInputException("user not found", Map.of("userId", "user not exist")));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("news not found", Map.of("newsId", "news not exist")));

        return newsInteractionRepository.findByUserAndNews(user, news)
                .orElseThrow(() -> new InvalidInputException("newsInteraction not found", Map.of("newsInteraction", "user does not have any Interaction for news")));
    }
}
