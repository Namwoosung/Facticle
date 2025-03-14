package com.example.facticle.user.dto;

import com.example.facticle.common.service.DateTimeUtil;
import com.example.facticle.user.entity.SignupType;
import com.example.facticle.user.entity.User;
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

    public static GetProfileResponseDto from(User user){
        return GetProfileResponseDto.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .username(user.getSignupType() == SignupType.LOCAL ? user.getLocalAuth().getUsername() : null)
                .socialId(user.getSignupType() == SignupType.SOCIAL ? user.getSocialAuth().getSocialId() : null)
                .socialProvider(user.getSignupType() == SignupType.SOCIAL ? user.getSocialAuth().getSocialProvider() : null)
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .signupType(user.getSignupType())
                .createdAt(DateTimeUtil.convertUTCToKST(user.getCreatedAt()))
                .updatedAt(DateTimeUtil.convertUTCToKST(user.getUpdatedAt()))
                .lastLogin(DateTimeUtil.convertUTCToKST(user.getLastLogin()))
                .build();
    }

}
