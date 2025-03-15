package com.example.facticle.news.dto;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SortBy {
    COLLECTED_AT("collectedAt"),
    LIKE_COUNT("likeCount"),
    HATE_COUNT("hateCount"),
    VIEW_COUNT("viewCount"),
    HEADLINE_SCORE("headlineScore"),
    FACT_SCORE("factScore"),
    RATING("rating");

    private final String value;

    SortBy(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
