package com.example.facticle.news.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "news_index")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Builder
public class NewsDocument {
    @Id
    @Field(type = FieldType.Keyword)
    private String newsId;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String title;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String content;
}
