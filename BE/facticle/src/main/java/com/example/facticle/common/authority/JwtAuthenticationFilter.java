package com.example.facticle.common.authority;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
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
        if(StringUtils.hasText(token)){
            TokenValidationResult tokenValidationResult = jwtTokenProvider.validateToken(token); //검사 결과를 가져옴

            if(tokenValidationResult == TokenValidationResult.VALID){ //유효하다면 Authentication 저장
                if(!jwtTokenProvider.getTokenType(token).equals("ACCESS")){ //access token이 아닌경우
                    log.warn("Invalid Token Type: Only Access Token is allowed");
                    throw new InsufficientAuthenticationException("Invalid Token Type: Only Access Token is allowed"); //filter 내부에서 발생한 인증오류 이므로 AuthenticationEntryPoint를 호출하게 됨
                }

                //토큰의 정보를 기반으로 Authentication 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);

                //SecurityContextHolder에 Authentication 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (tokenValidationResult == TokenValidationResult.EXPIRED) { //만료라면
                request.setAttribute("expired_token", true); //request를 활용해 만료 여부 저장
            }else{ //이외의 다른 이유라면
                request.setAttribute("expired_token", false); //request를 활용해 만료 여부 저장
            }
        }
        filterChain.doFilter(request, response); //만약 토큰이 유효하지 않아 SecurityContextHolder에 저장된 인증정보가 없으면 spring security가 내부적으로 인증 오류로 판단해 AuthenticationEntryPoint를 호출하게 됨
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
