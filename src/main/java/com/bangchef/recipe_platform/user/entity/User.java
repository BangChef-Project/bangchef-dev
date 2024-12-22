package com.bangchef.recipe_platform.user.entity;

import com.bangchef.recipe_platform.common.enums.Role;
import com.bangchef.recipe_platform.report.entity.Report;
import com.bangchef.recipe_platform.security.token.entity.RefreshToken;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @Builder.Default
    @Column(name = "subscribers")
    private Integer subscribers = 0;

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    @Builder.Default
    @Column(name = "avg_rating")
    private Float avgRating = 0.0f;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private boolean enabled = false; // 초기 비활성화 상태

    @Column(unique = true)
    private String verificationToken; // 이메일 인증 토큰

    @Column(unique = true)
    private String fcmToken; // fcm 토큰

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "reportedUser", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reportedReports;

}
