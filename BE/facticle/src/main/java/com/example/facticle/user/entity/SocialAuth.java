package com.example.facticle.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter //부주의한 공유 참조로 인한 문제를 예방하기 위해 불변객체로 생성
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAuth {
    @Size(max = 30, message = "social login provider must not exceed 30 characters")
    @Column(length = 30)
    private String socialProvider;
    @Size(max = 255, message = "social id must not exceed 255 characters")
    private String socialId;


    //값 비교는 equals로 비교
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialAuth that = (SocialAuth) o;
        return Objects.equals(getSocialProvider(), that.getSocialProvider()) && Objects.equals(getSocialId(), that.getSocialId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSocialProvider(), getSocialId());
    }
}
