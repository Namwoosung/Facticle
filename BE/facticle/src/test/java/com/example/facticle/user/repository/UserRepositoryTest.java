package com.example.facticle.user.repository;

import com.example.facticle.user.entity.LocalAuth;
import com.example.facticle.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("local")
@Transactional
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    public void basicUserTest(){

        //given
        LocalAuth localAuth = new LocalAuth("user1", "testpassword");
        User user = new User(localAuth, "nick1");


        ///when
        User savedUser = userRepository.save(user);

        //then
        User findUser = userRepository.findById(savedUser.getUserId()).get();
        Assertions.assertThat(findUser.getUserId()).isEqualTo(savedUser.getUserId());
        Assertions.assertThat(findUser.getNickname()).isEqualTo("nick1");
        Assertions.assertThat(findUser).isEqualTo(savedUser);


    }

}