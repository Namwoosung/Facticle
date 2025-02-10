package com.example.facticle.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NicknameCheckDto {
    @NotBlank(message = "Nickname is required")
    @Size(min = 2, max = 20, message = "Nickname must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣-_]+$", message = "Nickname can only contain Korean, English letters, numbers, underscores, and dashes")
    private String nickname;
}
