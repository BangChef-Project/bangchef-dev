package com.bangchef.recipe_platform.recipe.dto;

import com.bangchef.recipe_platform.common.enums.Difficulty;
import com.bangchef.recipe_platform.common.enums.RecipeCategory;
import com.bangchef.recipe_platform.common.enums.RecipeSortType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class RequestRecipeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoriesBySortDto {
        private RecipeCategory[] categories;
        private RecipeSortType sortType;
    }

    @Data
    public static class Create {
        @NotBlank(message = "레시피 제목은 필수입니다.")
        private String title;

        @NotBlank(message = "레시피 설명은 필수입니다.")
        private String description;

        @NotBlank(message = "재료 입력은 필수입니다.")
        private String ingredients;

        @NotNull(message = "요리 카테고리를 선택해야 합니다.")
        private RecipeCategory category;

        @NotNull(message = "조리 난이도를 선택해야 합니다.")
        private Difficulty difficulty;

        @Min(value = 1, message = "조리 시간은 1분 이상이어야 합니다.")
        private int cookTime;

        @Pattern(
                regexp = "^(http|https)://.*$",
                message = "이미지 URL은 http 또는 https로 시작해야 합니다."
        )
        private String imageUrl;

        @NotEmpty(message = "조리 단계는 최소 1개 이상이어야 합니다.")
        private List<CookingStepDto> cookingStepDtoList = new ArrayList<>();
    }

    @Data
    @Builder
    public static class CookingStepDto {
        @NotNull(message = "조리단계 번호를 선택해야 합니다.")
        private int stepNumber; // 조리 단계 번호

        @NotBlank(message = "조리 설명은 필수입니다.")
        private String description; // 조리 순서 설명

        @Pattern(
                regexp = "^(http|https)://.*$",
                message = "이미지 URL은 http 또는 https로 시작해야 합니다."
        )
        private String imageUrl; // 조리 단계 이미지 URL
    }

    @Data
    public static class Update {
        @NotNull(message = "레시피 ID는 필수입니다.")
        private Long id;

        @NotBlank(message = "레시피 제목은 필수입니다.")
        private String title;

        @NotBlank(message = "레시피 설명은 필수입니다.")
        private String description;

        @NotBlank(message = "재료 입력은 필수입니다.")
        private String ingredients;

        @NotNull(message = "요리 카테고리를 선택해야 합니다.")
        private RecipeCategory category;

        @NotNull(message = "조리 난이도를 선택해야 합니다.")
        private Difficulty difficulty;

        @Min(value = 1, message = "조리 시간은 1분 이상이어야 합니다.")
        private int cookTime;

        @Pattern(
                regexp = "^(http|https)://.*$",
                message = "이미지 URL은 http 또는 https로 시작해야 합니다."
        )
        private String imageUrl;

        @NotEmpty(message = "조리 단계는 최소 1개 이상이어야 합니다.")
        private List<CookingStepDto> cookingStepDtoList = new ArrayList<>();
    }

    @Data
    public static class Ranking {

        private int page = 0; // 페이지 번호 (기본값 0)
        private int size = 10; // 페이지 크기 (기본값 10)

        @NotNull(message = "정렬 기준은 필수입니다.")
        private RankingCriteria criteria; // 정렬 기준 (종합, 조회수, 댓글수, 평균별점, 즐겨찾기 수)

        public enum RankingCriteria {
            OVERALL,       // 종합 순위 (조회수, 평균별점, 즐겨찾기 등을 종합적으로 계산)
            VIEWS,         // 조회수 기준
            COMMENTS_COUNT, // 댓글 수 기준
            AVG_RATING,    // 평균 별점 기준
            FAVORITES_COUNT // 즐겨찾기 수 기준
        }
    }

}