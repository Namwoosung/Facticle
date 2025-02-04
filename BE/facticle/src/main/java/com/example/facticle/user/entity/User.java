package com.example.facticle.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "Users",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = "nickname"),
            @UniqueConstraint(columnNames = "username"),
            @UniqueConstraint(columnNames = {"socialProvider", "socialId"})
    }
)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Embedded
    private LocalAuth localAuth;

    @NotBlank(message = "Nickname is required")
    @Size(max = 20, message = "nickname must not exceed 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣-_]{1,20}$", message = "Nickname can only contain letters, numbers, underscores, and dashes")
    @Column(nullable = false, unique = true)
    private String nickname;
    @Email(message = "email should be valid")
    @Size(max = 255, message = "email must not exceed 255 characters")
    @Column(unique = true)
    private String email;
    @Size(max = 1024, message = "profile URL must not exceed 1024 characters")
    private String profileImage;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private UserRole role = UserRole.USER;
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private SignupType signupType = SignupType.LOCAL;

    @CreationTimestamp //엔티티가 처음 생성될 때의 시간을 자동 저장
    @Column(updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
    @UpdateTimestamp //Insert나 Update 시 마다 해당 시간을 저장
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;
    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastLogin;

    @Embedded
    private SocialAuth socialAuth;

    public User(LocalAuth localAuth, String nickname) {
        this.localAuth = localAuth;
        this.nickname = nickname;
    }

    /*
    //일단 역방향 참조는 구현 x, 현재 요구사항 대로면 userActivity의 경우에는 마이페이지 조회 시에만 필요하므로 굳이 크게 중요한 필드는 아닐 것
    //추후 비즈니스 요구사항이 변경되어서 필요하면 역방향 연관관계까지 추가 설정
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    //UserActivity 클래스의 user 필드와 매핑
    //cascadeType.ALl로 모든 상태변화에 대해 전파 -> 즉 user를 persist하면 당시 user내에 있는 userActivities들도 persist됨
    //userActivities는 user에만 종속적이고, user와 userActivities는 모든 영속화 과정을 맞추는 것이 맞기에 ALL로 설정
    //User테이블에서 userActivities에서 아이템을 제거하면 해당 userActivity 데이터도 삭제 <- 즉 user가 사라지면 해당 user의 useractivies도 모두 사라져야 하므로 설정해 놓는 것이 맞음
    //결론적으로 위의 설정은 UserActivity가 관계에서 다 이므로 주인이지만, user와 useractivity는 모든 라이프사이클을 공유시켜 놓는 것
    private List<UserActivity> userActivities = new ArrayList<>();

    //연관관계 편의 메서드
    public void addUserActivity(UserActivity userActivity){
        userActivities.add(userActivity);
        userActivity.setUser(this);
    }
    */

}
