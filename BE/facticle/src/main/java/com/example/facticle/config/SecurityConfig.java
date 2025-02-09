package com.example.facticle.config;

import com.example.facticle.common.authority.JwtAuthenticationFilter;
import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.service.CustomUserDetailsService;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * security config 관련 내용은 노션에 정리함
 */
@Configuration
@EnableWebSecurity  //생략가능(security 설정을 가진 config가 있으면 자동 활성화 됨), spring security 활성화하는 역할(SecurityFilterChain 등록, UserDetailsService 설정, SecurityFilterChain 커스터마이징 등)
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

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
                            "/users/check-nickname"
                    ).permitAll()
                    .requestMatchers( // 🔹 인증 필요 api
                            "/users/logout",
                            "/users/profile",
                            "/users/profile-image",
                            "/users/mypage"
                    ).authenticated()
                    .requestMatchers("/admin/**").hasRole("ADMIN") //어드민 api 요청은 ADMIN 역할만 접근 가능
                    .anyRequest().authenticated() //그 외 요청은 모두 인증 필요
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    //추후 CORS 설정 추가
    //Exception Handling 추가
    //rateLimitFilter 추가(login이나 signup에서 브루트 포스 공격을 방지하기 위한 방어 기법)


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
