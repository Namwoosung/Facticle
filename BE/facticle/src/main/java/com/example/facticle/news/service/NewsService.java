package com.example.facticle.news.service;

import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.news.dto.*;
import com.example.facticle.news.entity.*;
import com.example.facticle.news.repository.jpa.CommentInteractionRepository;
import com.example.facticle.news.repository.jpa.CommentRepository;
import com.example.facticle.news.repository.jpa.NewsInteractionRepository;
import com.example.facticle.news.repository.jpa.NewsRepository;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsInteractionRepository newsInteractionRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentInteractionRepository commentInteractionRepository;

    public GetNewsResponseDto getNewsPage(Long newsId, Long userId, String viewedNewsIdsCookie, HttpServletResponse response) {
        //일단 일반적인 방식으로 조회하는 방식을 사용
        //추후 성능일 비교하고 페치조인을 사용하는 것이 적합한 지 비교 후 상황에 맞게 리패토링

        //유저 조회
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidInputException("invalid input", Map.of("userId", "user not found")));
        }

        //뉴스 조회
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("invalid input", Map.of("newsId", "news not found")));

        //조회 수 관련 로직
        //유저라면 NewsInteraction 존재 여부로 판단
        //비회원이라면 쿠키로 판단
        boolean shouldIncreaseViewCount = false;

        if(user != null){
            Optional<NewsInteraction> optionalInteraction = newsInteractionRepository.findByUserAndNews(user, news);
            if(optionalInteraction.isPresent()){
                optionalInteraction.get().updateViewedAt(LocalDateTime.now());
            }else{
                shouldIncreaseViewCount = true;
                NewsInteraction newInteraction = NewsInteraction.builder()
                        .viewedAt(LocalDateTime.now())
                        .build();
                newInteraction.updateUser(user);
                newInteraction.updateNews(news);
                newsInteractionRepository.save(newInteraction);
            }
        }else{
            Set<String> viewedIds = new HashSet<>();
            if (viewedNewsIdsCookie != null && !viewedNewsIdsCookie.isBlank()) {
                viewedIds.addAll(Arrays.asList(viewedNewsIdsCookie.split("-")));
            }

            if (!viewedIds.contains(newsId.toString())) {
                shouldIncreaseViewCount = true;
                viewedIds.add(newsId.toString());

                ResponseCookie updatedCookie = ResponseCookie.from("viewed_news_ids", String.join("-", viewedIds))
                        .httpOnly(true)
                        .path("/")
                        .maxAge(60 * 60 * 24) // 1일
                        .build();

                response.addHeader("Set-Cookie", updatedCookie.toString());
            }
        }

        //동시성 문제를 방지하기 위해 조회수를 바로 1 증가
        if (shouldIncreaseViewCount) {
            newsRepository.incrementViewCount(newsId);
        }


        //뉴스 정보 조회(viewCount 반영을 위해 새로 조회)
        news = newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("invalid input", Map.of("newsId", "news not found")));
        GetNewsDto getNewsDto = GetNewsDto.from(news);

        //댓글 정보 조회
        List<Comment> comments = commentRepository.findAllByNewsWithUser(news);
        List<GetCommentDto> getCommentDtos = new ArrayList<>();
        if(comments != null && !comments.isEmpty()){
            getCommentDtos = buildCommentDtoTree(comments);
        }


        //유저가 존재한다면, 해당 유저의 뉴스 인터랙션, 댓글 인터랙션 정보를 추가
        GetNewsInteractionDto getNewsInteractionDto = null;
        List<GetCommentInteractionDto> getCommentInteractionDtos = new ArrayList<>();
        if(user != null){
            getNewsInteractionDto = newsInteractionRepository.findByUserAndNews(user, news)
                    .map(GetNewsInteractionDto::from)
                    .orElse(null);

            if(comments != null && !comments.isEmpty()){
                List<CommentInteraction> commentInteractions = commentInteractionRepository.findAllByUserAndComments(user, comments);

                getCommentInteractionDtos = commentInteractions.stream()
                        .map(GetCommentInteractionDto::from)
                        .collect(Collectors.toList());
            }
        }

        //DTO를 추가해서 댓글정보도 함께 전달
        return GetNewsResponseDto.builder()
                .isUser(user != null)
                .getNewsDto(getNewsDto)
                .getCommentDtos(getCommentDtos)
                .getNewsInteractionDto(getNewsInteractionDto)
                .getCommentInteractionDtos(getCommentInteractionDtos)
                .build();
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

    public GetCommentDto createComment(Long newsId, Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidInputException("user not found", Map.of("userId", "user not exist")));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("news not found", Map.of("newsId", "news not exist")));

        Comment comment = Comment.builder()
                .content(content)
                .build();
        comment.updateUser(user);
        comment.updateNews(news);

        commentRepository.save(comment);
        news.increaseCommentCount();

        return GetCommentDto.from(comment);
    }

    public GetCommentDto updateComment(Long newsId, Long commentId, Long userId, String content) {
        Comment comment = commentRepository.findWithUserAndNewsById(commentId)
                .orElseThrow(() -> new InvalidInputException("comment not found", Map.of("commentId", "comment not exist")));

        if(!comment.getUser().getUserId().equals(userId)){
            throw new InvalidInputException("user not matched",  Map.of("userId", "user not matched"));
        }
        if(!comment.getNews().getNewsId().equals(newsId)){
            throw new InvalidInputException("news not matched",  Map.of("newsId", "news not matched"));
        }

        comment.updateContent(content, LocalDateTime.now());

        return GetCommentDto.from(comment);
    }

    public void deleteComment(Long newsId, Long commentId, Long userId) {
        Comment comment = commentRepository.findWithUserAndNewsById(commentId)
                .orElseThrow(() -> new InvalidInputException("comment not found", Map.of("commentId", "comment not exist")));

        if(!comment.getUser().getUserId().equals(userId)){
            throw new InvalidInputException("user not matched",  Map.of("userId", "user not matched"));
        }
        if(!comment.getNews().getNewsId().equals(newsId)){
            throw new InvalidInputException("news not matched",  Map.of("newsId", "news not matched"));
        }

        comment.getNews().decreaseCommentCount();

        commentRepository.delete(comment);
    }



    private NewsInteraction getExistingNewsInteraction(Long newsId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidInputException("user not found", Map.of("userId", "user not exist")));
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new InvalidInputException("news not found", Map.of("newsId", "news not exist")));

        return newsInteractionRepository.findByUserAndNews(user, news)
                .orElseThrow(() -> new InvalidInputException("newsInteraction not found", Map.of("newsInteraction", "user does not have any Interaction for news")));
    }

    private List<GetCommentDto> buildCommentDtoTree(List<Comment> comments){
        Map<Long, GetCommentDto> dtoMap = new LinkedHashMap<>();
        List<GetCommentDto> roots = new ArrayList<>();

        for (Comment comment : comments) {
            GetCommentDto getCommentDto = GetCommentDto.from(comment);
            dtoMap.put(getCommentDto.getCommentId(), getCommentDto);
            if(getCommentDto.getParentCommentId() == null){
                roots.add(getCommentDto);
            }
        }

        for (Comment comment : comments) {
            if (comment.getParentComment() != null) {
                Long parentId = comment.getParentComment().getCommentId();
                GetCommentDto parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getReplies().add(dtoMap.get(comment.getCommentId()));
                }
            }
        }

        return roots;
    }



}
