package com.example.facticle.user.service;

import com.example.facticle.common.authority.JwtTokenProvider;
import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.common.authority.TokenInfo;
import com.example.facticle.user.dto.LocalLoginRequestDto;
import com.example.facticle.user.dto.LocalSignupRequestDto;
import com.example.facticle.user.entity.LocalAuth;
import com.example.facticle.user.entity.SignupType;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.entity.UserRole;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public Long saveUser(LocalSignupRequestDto localSignupRequestDto){
        checkLocalSignupDto(localSignupRequestDto);

        String hashedPassword = passwordEncoder.encode(localSignupRequestDto.getPassword());
        LocalAuth localAuth = new LocalAuth(localSignupRequestDto.getUsername(), hashedPassword);
        User user = User.builder()
                .localAuth(localAuth)
                .nickname(localSignupRequestDto.getNickname())
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    private void checkLocalSignupDto(LocalSignupRequestDto localSignupRequestDto) {
        Map<String, String> errors = new HashMap<>();

        //만약 username이 동일한 경우 혹은 nickname이 동일한 경우라면 illegalArgumentException 예외를 발생하는 로직 추가
        if(userRepository.existsByLocalAuthUsername(localSignupRequestDto.getUsername())){
            errors.put("username", "username already exists.");
        }

        if(userRepository.existsByNickname(localSignupRequestDto.getNickname())){
            errors.put("nickname", "nickname already exists.");
        }

        if(!errors.isEmpty()){
            throw new InvalidInputException("Invalid input", errors);
        }
    }

    public boolean checkUsername(String username) {
        return !userRepository.existsByLocalAuthUsername(username);
    }

    public boolean checkNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * 최초 로그인 시 spring security 전 과정을 통해 로그인 정보를 검증 => 검증이 완료되면 반환된 Authentication 객체를 통해 token을 생성
     * DB에서 username으로 user를 찾고, 비밀번호를 검증하는 과정은 authenticationManager.authenticate() 과정에서 이루어짐
     */
    public TokenInfo localLogin(LocalLoginRequestDto localLoginRequestDto) {
        //인증을 위한 UsernamePasswordAuthenticationToken을 생성
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken
                = new UsernamePasswordAuthenticationToken(localLoginRequestDto.getUsername(), localLoginRequestDto.getPassword());

        //spring security를 통해 인증 수행 => authenticationManager에게 생성한 토큰 인증을 요구, 인증 완료 후 결과를 Authentication으로 받음
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);

        //인증받은 Authentication을 통해 token을 발급 받음
        String accessToken = jwtTokenProvider.createAccessToken(authenticate);
        String refreshToken = jwtTokenProvider.createRefreshToken(authenticate);

        return new TokenInfo("Bearer", accessToken, refreshToken);
    }
}
