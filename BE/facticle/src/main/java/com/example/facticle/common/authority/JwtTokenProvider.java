package com.example.facticle.common.authority;

import com.example.facticle.common.dto.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-valid-time}")
    private long accessTokenValidTime;

    @Value("${jwt.refresh-token-valid-time}")
    private long refreshTokenValidTime;

    private Key key;

    @PostConstruct
    public void init(){
        //JWT를 서명할 때 사용하는 key는 각 서명화 알고리즘에 맞는 키 타입을 사용해야 함
        //(Jwts.builder().signWith(key, SignatureAlgorithm.HS256)에서 key 파라미터는 SecretKey 또는 Key 객체를 인자로 받음(string은 안 됨))
        //여기선 HS256을 사용할 예정이니 hmacShaKeyFor()로 secret key를 key타입으로 바꿔주는 과정을 거침
        //현재 secretKey가 BASE64인코딩 되어 있으므로 디코딩해주고, 해당 값으로 key를 생성
        //secretKey가 BSAE64 인코딩 되어 있는 문자열이 아니라면 예외 발생 가능 => 추후 더 안전하게 리팩토링 하려면 secretkey가 BASE64 형식이 아닌 경우 exception 발생하도록 보완 가능
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }


    public String createAccessToken(Authentication authentication){
        return createToken(authentication, accessTokenValidTime);
    }

    public String createRefreshToken(Authentication authentication){
        return createToken(authentication, refreshTokenValidTime);
    }

    /**
     * 토큰 생성
     */
    private String createToken(Authentication authentication, Long tokenValidTime){
        //authentication에 있는 권한 정보들을 모두 가져와 ,로 나눠 하나의 string으로 생성
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        //authentication에 저장된 principal로 userId 획득
        if (!(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new IllegalArgumentException("Authentication principal is not an instance of CustomUserDetails");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();

        //현재 시간 및 만료 시간 저장
        Date now = new Date();
        Date expiration = new Date(now.getTime() + tokenValidTime);

        //JWT 발급
        return Jwts.builder()
                .setSubject(userDetails.getUsername()) //username을 제목으로 활용
                .claim("auth", authorities)
                .claim("userId", userId) //claim에도 userId를 입력
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token); //서명 검증 및 클레임 파싱
            return true;
        } catch (ExpiredJwtException e) { //토큰이 만료된 경우
            log.warn("Expired JWT Token: {}", e.getMessage());
        } catch (UnsupportedJwtException e) { //토큰의 형식이나 구조가 이상한 경우
            log.warn("Unsupported JWT Token: {}", e.getMessage());
        } catch (MalformedJwtException e) { //토큰이 유효하지 않은 경우
            log.warn("Invalid JWT Token: {}", e.getMessage());
        } catch (io.jsonwebtoken.security.SecurityException e) { //서명이 올바르지 않은 경우
            log.warn("Invalid JWT Signature: {}", e.getMessage());
        } catch (IllegalArgumentException e) { //토큰이 비어있거나 null인 경우
            log.warn("JWT Claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token){
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public Long getUserId(String token){
        Claims claims = getClaims(token);
        return claims.get("userId", Long.class);
    }

    public List<GrantedAuthority> getAuthorities(String token){
        Claims claims = getClaims(token);
        String auth = claims.get("auth", String.class);
        return Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    //토큰 정보 추출 -> 토큰 검증이 완료된 후 SecurityContextHolder에 저장할 Authentication 객체 생성을 위함
    public Authentication getAuthentication(String token){
        Claims claims = getClaims(token);

        String auth = claims.get("auth", String.class);
        List<GrantedAuthority> authorities = Arrays.stream(auth.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        //인증이 완료된 객체는 일반적으로 비밀번호를 공백으로 설정
        UserDetails principal = new CustomUserDetails(claims.get("userId", Long.class), claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
