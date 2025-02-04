package com.example.facticle.user.repository;

import com.example.facticle.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLocalAuthUsername(String username);
    boolean existsByNickname(String nickname);

}
