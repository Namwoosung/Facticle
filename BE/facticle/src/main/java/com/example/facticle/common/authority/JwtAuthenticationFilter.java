package com.example.facticle.common.authority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security Config에 등록되어 필터역할을 할 클래스
 * 매 요청마다 JWT 토큰을 검증하고,해당 토큰의 사용자 정보를 SecurityContext에 저장하는 역할
 * (JWT 인증방식의 경우 토큰만 검증하고, 토큰이 검증되는 것으로 요청 허용. 세션방식처럼 다시 전체 spring security 인증과정을 거치지 않음)
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);

        //token이 존재하고, 유효한 경우
        if(StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)){
            //토큰의 정보를 기반으로 Authentication 생성
            Authentication authentication = jwtTokenProvider.getAuthentication(token);

            //SecurityContextHolder에 Authentication 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);


    }

    //request의 header에서 JWT 토큰을 획득
    private String resolveToken(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
