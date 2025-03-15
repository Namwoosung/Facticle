package com.example.facticle.news.repository;

import com.example.facticle.news.dto.NewsSearchCondition;
import com.example.facticle.news.dto.SortBy;
import com.example.facticle.news.dto.SortDirection;
import com.example.facticle.news.entity.News;
import com.example.facticle.news.entity.NewsCategory;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.facticle.news.entity.QNews.news;


@Repository
@RequiredArgsConstructor
public class NewsRepositoryCustomImpl implements NewsRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<News> searchNewsList(NewsSearchCondition condition) {
        return jpaQueryFactory
                .select(news)
                .from(news)
                .where(
                        titleKeywordIn(condition.getTitleKeyword()),
                        contentKeywordIn(condition.getContentKeyword()),
                        titleOrContentKeywordIn(condition.getTitleOrContentKeyword()),
                        publishersIn(condition.getPublishers()),
                        categoriesIn(condition.getCategories()),
                        collectedAtBetween(condition.getStartDate(), condition.getEndDate()),
                        headlineScoreBetween(condition.getMinHsScore(), condition.getMaxHsScore()),
                        factScoreBetween(condition.getMinFsScore(), condition.getMaxFsScore()),
                        likesBetween(condition.getMinLikes(), condition.getMaxLikes()),
                        dislikesBetween(condition.getMinDislikes(), condition.getMaxDislikes()),
                        commentsBetween(condition.getMinComments(), condition.getMaxComments()),
                        viewsBetween(condition.getMinViews(), condition.getMaxViews()),
                        ratingCountBetween(condition.getMinRatingCount(), condition.getMaxRatingCount()),
                        ratingBetween(condition.getMinRating(), condition.getMaxRating())
                )
                .orderBy(getOrderSpecifier(condition.getSortBy(), condition.getSortDirection()))
                .offset((long) condition.getPage() * condition.getSize())
                .limit(condition.getSize())
                .fetch();
    }

    /**
     * 추후 ElasticSearch 적용 시 검색된 뉴스 ID 리스트를 받아서 필터링할 예정
     */
    private BooleanExpression titleKeywordIn(String titleKeyword) {
        return null; // ElasticSearch 적용 후 뉴스 ID 리스트 필터링 추가 예정
    }

    private BooleanExpression contentKeywordIn(String contentKeyword) {
        return null; // ElasticSearch 적용 후 뉴스 ID 리스트 필터링 추가 예정
    }

    private BooleanExpression titleOrContentKeywordIn(String titleOrContentKeyword) {
        return null; // ElasticSearch 적용 후 뉴스 ID 리스트 필터링 추가 예정
    }


    // 언론사 포함 조건
    private BooleanExpression publishersIn(List<String> publishers){
        return (publishers != null && !publishers.isEmpty()) ? news.mediaName.in(publishers) : null;
    }

    // 카테고리 포함 조건
    private BooleanExpression categoriesIn(List<NewsCategory> categories){
        return (categories != null && !categories.isEmpty()) ? news.category.in(categories) : null;
    }

    // 시간 범위 조건
    private BooleanExpression collectedAtBetween(LocalDateTime startDate, LocalDateTime endDate){
        if(startDate != null && endDate != null){
            return news.collectedAt.between(startDate, endDate);
        } else if (startDate != null) {
            return news.collectedAt.goe(startDate);
        } else if (endDate != null) {
            return news.collectedAt.loe(endDate);
        }
        return null;
    }

    // hs 점수 조건
    private BooleanExpression headlineScoreBetween(BigDecimal minHsScore, BigDecimal maxHsScore){
        if (minHsScore != null) {
            minHsScore = minHsScore.setScale(2, RoundingMode.HALF_UP);
        }
        if (maxHsScore != null) {
            maxHsScore = maxHsScore.setScale(2, RoundingMode.HALF_UP);
        }

        if (minHsScore != null && maxHsScore != null) {
            return news.headlineScore.between(minHsScore, maxHsScore);
        } else if (minHsScore != null) {
            return news.headlineScore.goe(minHsScore);
        } else if (maxHsScore != null) {
            return news.headlineScore.loe(maxHsScore);
        }
        return null;
    }

    // fs 점수 조건
    private BooleanExpression factScoreBetween(BigDecimal minFsScore, BigDecimal maxFsScore) {
        if (minFsScore != null) {
            minFsScore = minFsScore.setScale(2, RoundingMode.HALF_UP);
        }
        if (maxFsScore != null) {
            maxFsScore = maxFsScore.setScale(2, RoundingMode.HALF_UP);
        }

        if (minFsScore != null && maxFsScore != null) {
            return news.factScore.between(minFsScore, maxFsScore);
        } else if (minFsScore != null) {
            return news.factScore.goe(minFsScore);
        } else if (maxFsScore != null) {
            return news.factScore.loe(maxFsScore);
        }
        return null;
    }

    // 좋아요 수 범위 조건
    private BooleanExpression likesBetween(Integer minLikes, Integer maxLikes) {
        if (minLikes != null && maxLikes != null) {
            return news.likeCount.between(minLikes, maxLikes);
        } else if (minLikes != null) {
            return news.likeCount.goe(minLikes);
        } else if (maxLikes != null) {
            return news.likeCount.loe(maxLikes);
        }
        return null;
    }

    // 싫어요 수 범위 조건
    private BooleanExpression dislikesBetween(Integer minDislikes, Integer maxDislikes) {
        if (minDislikes != null && maxDislikes != null) {
            return news.hateCount.between(minDislikes, maxDislikes);
        } else if (minDislikes != null) {
            return news.hateCount.goe(minDislikes);
        } else if (maxDislikes != null) {
            return news.hateCount.loe(maxDislikes);
        }
        return null;
    }

    // 댓글 숫 범위 조건
    private BooleanExpression commentsBetween(Integer minComments, Integer maxComments) {
        if (minComments != null && maxComments != null) {
            return news.commentCount.between(minComments, maxComments);
        } else if (minComments != null) {
            return news.commentCount.goe(minComments);
        } else if (maxComments != null) {
            return news.commentCount.loe(maxComments);
        }
        return null;
    }

    // 조회 수 범위 조건
    private BooleanExpression viewsBetween(Integer minViews, Integer maxViews) {
        if (minViews != null && maxViews != null) {
            return news.viewCount.between(minViews, maxViews);
        } else if (minViews != null) {
            return news.viewCount.goe(minViews);
        } else if (maxViews != null) {
            return news.viewCount.loe(maxViews);
        }
        return null;
    }

    // 별점 평가 수 범위 조건
    private BooleanExpression ratingCountBetween(Integer minRatingCount, Integer maxRatingCount) {
        if (minRatingCount != null && maxRatingCount != null) {
            return news.ratingCount.between(minRatingCount, maxRatingCount);
        } else if (minRatingCount != null) {
            return news.ratingCount.goe(minRatingCount);
        } else if (maxRatingCount != null) {
            return news.ratingCount.loe(maxRatingCount);
        }
        return null;
    }

    // 별점 범위 조건
    private BooleanExpression ratingBetween(BigDecimal minRating, BigDecimal maxRating) {
        if (minRating != null) {
            minRating = minRating.setScale(1, RoundingMode.HALF_UP);
        }
        if (maxRating != null) {
            maxRating = maxRating.setScale(1, RoundingMode.HALF_UP);
        }

        if (minRating != null && maxRating != null) {
            return news.rating.between(minRating, maxRating);
        } else if (minRating != null) {
            return news.rating.goe(minRating);
        } else if (maxRating != null) {
            return news.rating.loe(maxRating);
        }
        return null;
    }

    private OrderSpecifier<?> getOrderSpecifier(SortBy sortBy, SortDirection sortDirection){
        return switch (sortBy) {
            case COLLECTED_AT -> (sortDirection == SortDirection.ASC ? news.collectedAt.asc() : news.collectedAt.desc());
            case FACT_SCORE -> (sortDirection == SortDirection.ASC ? news.factScore.asc() : news.factScore.desc());
            case HEADLINE_SCORE -> (sortDirection == SortDirection.ASC ? news.headlineScore.asc() : news.headlineScore.desc());
            case VIEW_COUNT -> (sortDirection == SortDirection.ASC ? news.viewCount.asc() : news.viewCount.desc());
            case RATING -> (sortDirection == SortDirection.ASC ? news.rating.asc() : news.rating.desc());
            case LIKE_COUNT -> (sortDirection == SortDirection.ASC ? news.likeCount.asc() : news.likeCount.desc());
            case HATE_COUNT -> (sortDirection == SortDirection.ASC ? news.hateCount.asc() : news.hateCount.desc());
            default -> news.collectedAt.desc();
        };
    }

}
