package com.example.facticle.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * CustomUserDetailsService에서 사용할 UserDetails를 새롭게 정의
 * 기존의 UserDetails를 사용하지 않는 것은 userId와 같이 원하는 필드를 추가로 담기 위함 + 추후 코드 수정 시 확장성을 고려함
 */
@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    //아래의 메서드들은 추후 필요한 경우 확장
    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았다고 가정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠겨 있지 않다고 가정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호가 만료되지 않았다고 가정
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되었다고 가정
    }
}
