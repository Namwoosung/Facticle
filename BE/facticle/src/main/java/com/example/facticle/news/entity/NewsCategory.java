package com.example.facticle.news.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NewsCategory {
    POLITICS("politics"),
    ECONOMY("economy"),
    SOCIETY("society"),
    INTERNATIONAL("international"),
    TECH("tech"),
    CULTURE("culture"),
    ENTERTAINMENT("entertainment"),
    SPORTS("sports"),
    WEATHER("weather");

    private final String value;

    NewsCategory(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}