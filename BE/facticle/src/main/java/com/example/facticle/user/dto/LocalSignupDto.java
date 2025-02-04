package com.example.facticle.user.dto;


import com.example.facticle.user.entity.LocalAuth;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Getter @Setter
public class LocalSignupDto {

    //Validation을 위한 애노테이션 추후 추가

    @NotBlank(message = "username is required")
    @Size(max = 50, message = "username must not exceed 50 characters")
    private String username;

    @NotBlank(message = "password is required")
    @Size(max = 16, message = "password must not exceed 16 characters")
    private String password;

    @NotBlank(message = "nickname is required")
    @Size(max = 20, message = "nickname must not exceed 20 characters")
    private String nickname;
}
