package com.bangchef.recipe_platform.report.entity;

import com.bangchef.recipe_platform.common.enums.ReportStatus;
import com.bangchef.recipe_platform.user.entity.User;
import com.bangchef.recipe_platform.recipe.entity.Recipe;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // 신고자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = true)
    private User reportedUser; // 신고 대상 회원 (null일 수 있음)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_recipe_id", nullable = true)
    private Recipe reportedRecipe; // 신고 대상 게시글 (null일 수 있음)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason; // 신고 사유

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReportStatus status; // 신고 상태 (PENDING, RESOLVED, REJECTED)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 신고 날짜

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

