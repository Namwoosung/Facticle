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
public class LocalAuth {
    @Size(max = 50, message = "username must not exceed 50 characters")
    @Column(length = 50)
    private String username;
    @Size(max = 100, message = "hashed password must not exceed 100 characters")
    private String hashedPassword;

    //값 비교는 equals로 비교
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocalAuth localAuth = (LocalAuth) o;
        return Objects.equals(getUsername(), localAuth.getUsername()) && Objects.equals(getHashedPassword(), localAuth.getHashedPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getHashedPassword());
    }
}
