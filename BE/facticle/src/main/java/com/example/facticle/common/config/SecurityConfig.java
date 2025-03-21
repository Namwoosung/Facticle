package com.example.facticle.common.config;

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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

    //해시에 사용할 Encoder 지정, 일반적으로 많이 사용하는 BCryptPasswordEncoder를 사용
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
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) //CORS 활성화
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //세션 비활성화
            .formLogin(form -> form.disable()) //JWT 기반 인증 사용 -> form 로그인 비활성화
            .httpBasic(AbstractHttpConfigurer::disable) //JWT 기반 인증 사용 -> basicHttp 비활성화
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers( //인증 없이 접근 가능 api
                            "/api/users/login",
                            "/api/users/login/social",
                            "/api/users/signup",
                            "/api/users/signup/social",
                            "/api/users/check-username",
                            "/api/users/check-nickname",
                            "/api/users/token/refresh",
                            "/api/news/search",
                            "/api/news/{newsId}",
                            "/static/**",
                            "/favicon.ico"
                    ).permitAll()
                    .requestMatchers( // 🔹 인증 필요 api
                            "/api/users/logout",
                            "/api/users/profile",
                            "/api/users/profile-image",
                            "/api/users/mypage",
                            "/api/news/like",
                            "/api/news/hate"
                    ).authenticated()
                    .requestMatchers("/api/users/admin/**").hasRole("ADMIN") //어드민 api 요청은 ADMIN 역할만 접근 가능
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
     * CORS 설정(Cross-Origin Resource Sharing)
     * 웹 브라우저에서 보안 정책을 적용하는 방식, 기본적으로 다른 출처(Origin)에서 보내는 api 요청은 거부
     *  => 프론트 -> 백으로 보내는 요청도 거부됨(ex) localhost:3000 -> localhost:8080)
     * 즉 cross origin에서 어느 도메인으로 부터오는 어느 요청들을 허용할 것인지에 관한 설정
     * 이 CORS는 철저히 브라우저의 기준, 브라우저의 localhost:3000에서 localhost:8080으로 보내니 다른 도메인으로 인식
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true); // 쿠키를 포함한 요청 허용
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://frontend:3000",
                "https://localhost:3000",
                "https://frontend:3000")); // 허용할 프론트엔드 도메인
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // 허용할 메서드
        configuration.setAllowedHeaders(List.of("*")); //프론트엔드에서 요청을 보낼 때 포함할 수 있는 헤더
        configuration.setExposedHeaders(List.of("Authorization")); // 프론트에서 응답에서 조회할 수 있는 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정을 사용
        return source;
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

            boolean isExpired = Boolean.TRUE.equals(request.getAttribute("expired_token")); // request에 저장해놓은 만료 여부 확인

            BaseResponse errorResponse;
            if (isExpired) {
                errorResponse = BaseResponse.failure(
                        Map.of("code", 401, "is_expired", true),
                        "Access token has expired. Please refresh your token."
                );
            } else {
                errorResponse = BaseResponse.failure(
                        Map.of("code", 401, "is_expired", false),
                        "Authentication failed. Please provide a valid token."
                );
            }

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
