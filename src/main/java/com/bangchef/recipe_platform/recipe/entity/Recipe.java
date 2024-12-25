package com.bangchef.recipe_platform.recipe.entity;

import com.bangchef.recipe_platform.common.enums.Difficulty;
import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.report.entity.Report;
import com.bangchef.recipe_platform.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 255)
    private String title; // 레시피 제목

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description; // git 레시피 설명

    @Column(name = "ingredients", nullable = false, columnDefinition = "TEXT")
    private String ingredients; // 재료 리스트 (구분자로 구분)

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private RecipeCategory category; // 요리 카테고리 (한식, 양식 등)

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty; // 요리 난이도 (쉬움, 보통, 어려움)

    @Column(name = "cook_time", nullable = false)
    private Integer cookTime; // 조리시간 (분 단위)

    @Builder.Default
    @Column(name = "views", nullable = false)
    private Integer views = 0; // 조회수 (기본값 0)

    @Builder.Default
    @Column(name = "favorites_count", nullable = false)
    private Integer favoritesCount = 0; // 즐겨찾기 수 (기본값 0)

    @Builder.Default
    @Column(name = "avg_rating", nullable = false)
    private Float avgRating = 0.0f; // 평균 별점 (기본값 0.0)

    @Builder.Default
    @Column(name = "rating_count", nullable = false)
    private Integer ratingCount = 0; // 별점 수 (기본값 0)

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeRating> recipeRatingList = new ArrayList<>();

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 음식 이미지

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 작성일자 (기본값: 현재 시간)

    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepNumber ASC")
    private List<CookingStep> cookingStepList = new ArrayList<>(); // 조리순서

    @OneToMany(mappedBy = "reportedRecipe", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reports;

}