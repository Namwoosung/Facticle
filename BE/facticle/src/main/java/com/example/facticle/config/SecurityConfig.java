package com.example.facticle.config;

import com.example.facticle.common.authority.JwtAuthenticationFilter;
import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.dto.BaseResponse;
import com.example.facticle.common.service.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;
import java.util.Map;

/**
 * security config 관련 내용은 노션에 정리함
 */
@Configuration
@EnableWebSecurity  //생략가능(security 설정을 가진 config가 있으면 자동 활성화 됨), spring security 활성화하는 역할(SecurityFilterChain 등록, UserDetailsService 설정, SecurityFilterChain 커스터마이징 등)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    //비밀번호 해시에 사용
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain 설정
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) //CSRF 비활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 비활성화
            .formLogin(form -> form.disable()) //JWT 기반 인증 사용 -> form 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable) //JWT 기반 인증 사용 -> basicHttp 비활성화
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers( //인증 없이 접근 가능 api
                            "/users/login",
                            "/users/login/social",
                            "/users/signup",
                            "/users/signup/social",
                            "/users/check-username",
                            "/users/check-nickname",
                            "/static/**",
                            "/favicon.ico"
                    ).permitAll()
                    .requestMatchers( // 🔹 인증 필요 api
                            "/users/logout",
                            "/users/profile",
                            "/users/profile-image",
                            "/users/mypage"
                    ).authenticated()
                    .requestMatchers("/users/admin/**").hasRole("ADMIN") //어드민 api 요청은 ADMIN 역할만 접근 가능
                    .anyRequest().authenticated() //그 외 요청은 모두 인증 필요
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

            //filter에서 발생하는 예외를 처리
            //우리는 CustomExceptionHandler에서 대부분의 예외를 받아서 처리 => @RestControllerAdvice가 붙어있으면 Controller 수준까지 넘어온 예외를 받아서 처리 가능
            //그러나 filter의 경우 DispatcherServlet에 도달하기 전의 과정이기에 CustomExceptionHandler에서 처리할 수가 없음 => 따로 예외 handling을 해줘야 함
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler())
            );

        return http.build();
    }


    /**
     * 401 Unauthorized 예외 처리
     * - JWT 인증 실패, 토큰 없음, 토큰 만료 등의 상황
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            BaseResponse errorResponse = BaseResponse.failure(
                    Map.of("code", 401),
                    "Authentication failed. Please provide a valid token."
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }

    /**
     * 403 Forbidden 예외 처리
     * - 인증은 되었지만, 권한이 부족한 경우 (예: 일반 사용자가 ADMIN API 접근)
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            BaseResponse errorResponse = BaseResponse.failure(
                    Map.of("code", 403),
                    "Access denied. You do not have permission to access this resource."
            );

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        };
    }


    //추후 CORS 설정 추가
    //Exception Handling 추가



    /**
     * 우리가 작성한 customUserDetailsService와 passwordEncoder를 활용하는 authenticationProvider를 생성
     * 이후 ProviderManager에 authenticationProvider을 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(){
        //AuthenticationManager에 등록할 AuthenticationProvider를 새로 정의
        //DaoAuthenticationProvider는 DB 기반 인증을 수행하는 구현체
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        //등록할 AuthenticationProvider를 설정, 우리가 사용할 customUserDetailsService, 비밀번호 해시 객체를 세팅
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        //새로운 AuthenticationManager로서 ProviderManager구현체에 등록할 authenticationProvider를 넣고 설정
        return new ProviderManager(List.of(authenticationProvider));
    }
}
