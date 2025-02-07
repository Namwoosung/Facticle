package com.example.facticle.user.service;

import com.example.facticle.common.exception.InvalidInputException;
import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.dto.UsernameCheckDto;
import com.example.facticle.user.entity.LocalAuth;
import com.example.facticle.user.entity.SignupType;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.entity.UserRole;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Transactional
    public Long saveUser(LocalSignupDto localSignupDto){
        checkLocalSignupDto(localSignupDto);

        String hashedPassword = passwordEncoder.encode(localSignupDto.getPassword());
        LocalAuth localAuth = new LocalAuth(localSignupDto.getUsername(), hashedPassword);
        User user = new User(localAuth, localSignupDto.getNickname(), UserRole.USER, SignupType.LOCAL);

        userRepository.save(user);

        return user.getUserId();
    }

    private void checkLocalSignupDto(LocalSignupDto localSignupDto) {
        Map<String, String> errors = new HashMap<>();

        //만약 username이 동일한 경우 혹은 nickname이 동일한 경우라면 illegalArgumentException 예외를 발생하는 로직 추가
        if(userRepository.existsByLocalAuthUsername(localSignupDto.getUsername())){
            errors.put("username", "username already exists.");
        }

        if(userRepository.existsByNickname(localSignupDto.getNickname())){
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
}
