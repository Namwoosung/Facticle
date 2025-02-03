package com.example.facticle.user.service;

import com.example.facticle.user.dto.LocalSignupDto;
import com.example.facticle.user.entity.LocalAuth;
import com.example.facticle.user.entity.User;
import com.example.facticle.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long saveUser(LocalSignupDto localSignupDto){

        //만약 username이 동일한 경우 혹은 nickname이 동일한 경우라면 illegalArgumentException을 발생하는 로직 추가

        String hashedPassword = passwordEncoder.encode(localSignupDto.getPassword());
        LocalAuth localAuth = new LocalAuth(localSignupDto.getUsername(), hashedPassword);
        User user = new User(localAuth, localSignupDto.getNickname());

        userRepository.save(user);

        return user.getUserId();
    }

}
