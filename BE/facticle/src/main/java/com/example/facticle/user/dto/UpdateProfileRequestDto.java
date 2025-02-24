package com.example.facticle.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UpdateProfileRequestDto {
    @Size(min = 2, max = 20, message = "Nickname must be between 2 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣-_]+$", message = "Nickname can only contain Korean, English letters, numbers, underscores, and dashes")
    private String nickname;

    @Email(message = "Invalid email format")
    @Size(max = 255, message = "email must not exceed 255 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    private String email;
}
