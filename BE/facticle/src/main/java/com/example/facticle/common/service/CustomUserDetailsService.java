package com.example.facticle.common.service;

import com.example.facticle.common.dto.CustomUserDetails;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService
 * 기존의 UserDetailsService는 인터페이스, AuthenticationProvider로 부터 사용자 정보를 받아 DB 조회 후 UserDetails를 반환하는 역할
 * 이 인터페이스를 구현해서 DB에서 user정보를 받아와 CustomUserDetails를 생성해 반환
 * 이제 우리는 JwtTokenProvider에서 여기서 반환한 CustomUserDetails를 기반으로 만들어진 Authentication을 활용 가능
 * (getAuthorities(), getCredentials(), getPrincipal(), getDetails() 등을 가져올 수 있음)
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //추후 필요한 경우 UsernameNotFoundException를 CustomExceptionHandler에 추가
        User user = userRepository.findByLocalAuthUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("user not found by username"));

        //Local Singup 유저가 아니라면, 예외 발생 => authenticationManager.authenticate()과정은 localLogin 시에만 발생하므로 UserDetailsService는 lcoal user만 해당(social 로그인은 검증을 플랫폼에 위임하므로)
        if(user.getLocalAuth() == null) {
            throw new UsernameNotFoundException("LocalAuth not found for user");
        }

        //현재는 user의 role이 1개. 단순히 1개만 authorities에 추가
        //추후 role이 늘어나거나 여러 권한을 관리할 경우 추가하는 로직을 추가
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));


        //CustomUserDetails을 생성해 반환
        return new CustomUserDetails(user.getUserId(), user.getLocalAuth().getUsername(), user.getLocalAuth().getHashedPassword(), authorities);
    }
}
