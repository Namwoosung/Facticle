package com.example.facticle.user.dto;

import com.example.facticle.user.entity.SignupType;
import com.example.facticle.user.entity.UserRole;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class GetProfileResponseDto {
    private Long userId;
    private String username;
    private String socialProvider;
    private String socialId;
    private String nickname;
    private String email;
    private String profileImage;
    private UserRole role;
    private SignupType signupType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

}
