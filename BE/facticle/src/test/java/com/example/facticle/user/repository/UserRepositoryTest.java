package com.example.facticle.user.repository;

import com.example.facticle.user.entity.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void basicUserTest(){
        //given
        LocalAuth localAuth = new LocalAuth("user1", "testpassword");
        User user = User.builder()
                .localAuth(localAuth)
                .nickname("nick1")
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();


        ///when
        User savedUser = userRepository.save(user);

        //then
        User findUser = userRepository.findById(savedUser.getUserId()).get();
        Assertions.assertThat(findUser.getUserId()).isEqualTo(savedUser.getUserId());
        Assertions.assertThat(findUser.getNickname()).isEqualTo(savedUser.getNickname());
        Assertions.assertThat(findUser).isEqualTo(savedUser);
    }

    @Test
    public void basicCRUDTest(){
        //given
        User user1 = User.builder()
                .localAuth(new LocalAuth("user1", "1234"))
                .nickname("nick1")
                .role(UserRole.USER)
                .signupType(SignupType.LOCAL)
                .build();

        User user2 = User.builder()
                .socialAuth(new SocialAuth("google", "abcd@email.com"))
                .nickname("nick2")
                .role(UserRole.ADMIN)
                .signupType(SignupType.SOCIAL)
                .build();


        userRepository.save(user1);
        userRepository.save(user2);

        User savedUser1 = userRepository.findById(user1.getUserId()).get();
        User savedUser2 = userRepository.findById(user2.getUserId()).get();
        Assertions.assertThat(user1).isEqualTo(savedUser1);
        Assertions.assertThat(user2).isEqualTo(savedUser2);

        List<User> users = userRepository.findAll();
        Assertions.assertThat(users.size()).isEqualTo(2);

        long count = userRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        userRepository.delete(user1);
        userRepository.delete(user2);
        long deleteCount = userRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);
    }

}