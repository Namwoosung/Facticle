//package com.example.facticle.user.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//
//
//// 보완필요! 일단 핵심 기능먼저 마무리하고, 아래는 설계부터 어떻게 할 것인지 다시 생각해야 함
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@AllArgsConstructor
//@Builder
//@ToString(of = {"activityId", "activityType", "createdAt"})
//@Table(name = "user_activities")
//public class UserActivity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long activityId;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;
//
//    @Enumerated(EnumType.STRING)
//    @Column(length = 20, nullable = false)
//    private ActivityType activityType;
//
//    //private News targetNewsId; //추후 뉴스 엔티티 생성 후 연관관계 설정
//
//    @CreationTimestamp
//    @Column(updatable = false, columnDefinition = "TIMESTAMP")
//    private LocalDateTime createdAt;
//
//    //protected로 설정해서 외부에서는 호출하지 못하도록 함
//    protected void setUser(User user) {
//        this.user = user;
//    }
//}
