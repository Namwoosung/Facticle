package com.example.facticle.user.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileImageDto {
    //추후 필요한 경우 프로필 이미지에 대한 validation 추가
    private MultipartFile profileImage;
}
