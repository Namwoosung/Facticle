package com.example.facticle.user.repository;

import com.example.facticle.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

//    @Test
//    public void basicUserTest(){
//        //given
//        User user = new User();
//        user.setNickname("testUser");
//
//
//        ///when
//        User savedUser = userRepository.save(user);
//
//        //then
//        User findUser = userRepository.findById(savedUser.getUserId()).get();
//        Assertions.assertThat(findUser.getUserId()).isEqualTo(savedUser.getUserId());
//        Assertions.assertThat(findUser.getNickname()).isEqualTo("testUser");
//        Assertions.assertThat(findUser).isEqualTo(savedUser);
//
//
//    }

}